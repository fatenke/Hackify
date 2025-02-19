

package tn.esprit;

import models.Evaluation;
import models.Vote;
import services.EvaluationService;
import services.IService;
import services.VoteService;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        IService<Evaluation> service = new EvaluationService();
        IService<Vote> service2 = new VoteService();
       // Vote vote = new Vote(1,3,1,1,1,15,"2025-02-08");
        // service2.add(vote);
       // Evaluation e = new Evaluation(2, 15, 15, 1, 15, 15, "2025-02-08");
        //Evaluation e1 = new Evaluation(1, 1, 1, 1, 15, 15, "2025-02-09");
       Evaluation ee = new Evaluation(10,15,1,1,19,16, "2025-02-15");
       Vote vote1 = new Vote(15,"2025-02-13",3,1,1,1);
        // service.add(e);
       // service.add(e1);
        // service.update(e1);
         //service.add(ee);
        service.delete(ee);
        service2.add(vote1);
        // service.delete(e);
       // service.delete(e1);
        System.out.println(service.getAll());
        System.out.println(service2.getAll());
    }
}
