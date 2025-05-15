package services;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import models.Poll;
import models.PollOption;

@WebServlet("/poll")
public class PollServlet extends HttpServlet {
    private final PollService pollService = new PollService();
    private final PollVoteService voteService = new PollVoteService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        try {
            if ("create".equals(action)) {
                createPoll(req);
            } else if ("vote".equals(action)) {
                castVote(req);
            }
            resp.sendRedirect("polls.jsp?chatId=" + req.getParameter("chatId"));
        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur : " + e.getMessage());
        }
    }

    private void createPoll(HttpServletRequest req) {
        int chatId = Integer.parseInt(req.getParameter("chatId"));
        String question = req.getParameter("question");
        String[] optionTexts = req.getParameterValues("options");

        List<PollOption> options = new ArrayList<>();
        if (optionTexts != null) {
            for (String text : optionTexts) {
                options.add(new PollOption(0, text));
            }
        }

        Poll poll = new Poll(chatId, question, options);
        pollService.add(poll);
    }

    private void castVote(HttpServletRequest req) {
        int pollId = Integer.parseInt(req.getParameter("pollId"));
        int optionId = Integer.parseInt(req.getParameter("optionId"));
        int userId = getUserIdFromSession(req);

        Poll poll = pollService.getById(pollId);
        if (poll == null) {
            throw new IllegalStateException("Sondage non trouvé.");
        }
        if (poll.isClosed()) {
            throw new IllegalStateException("Sondage fermé.");
        }

        voteService.recordVote(pollId, optionId, userId);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect("polls.jsp?chatId=" + req.getParameter("chatId"));
    }

    private int getUserIdFromSession(HttpServletRequest req) {
        // Replace with your authentication logic
        return 1; // Dummy user ID for testing
    }
}