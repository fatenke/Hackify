import sys
import speech_recognition as sr

def speech_to_text(field_name):
    recognizer = sr.Recognizer()

    with sr.Microphone() as source:
        audio = recognizer.listen(source, timeout=5, phrase_time_limit=10)

    try:
        text = recognizer.recognize_google(audio, language='fr-FR')  # Use 'fr-FR' for French, adjust for your language
        return text
    except sr.WaitTimeoutError:
        return ""
    except sr.UnknownValueError:
        return ""
    except sr.RequestError:
        return ""

if __name__ == "__main__":
    if len(sys.argv) > 1:
        field_name = sys.argv[1]
        text = speech_to_text(field_name)
        if text:
            print(text)  # Output only the transcribed text