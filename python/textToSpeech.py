import sys
import pyttsx3

def text_to_speech(text):
    # Initialize the TTS engine
    engine = pyttsx3.init()
    
    # Set properties (optional)
    engine.setProperty('rate', 150)  # Speed of speech
    engine.setProperty('volume', 0.9)  # Volume (0.0 to 1.0)
    
    # Convert text to speech
    engine.say(text)
    engine.runAndWait()

if __name__ == "__main__":
    # Get the text from command-line argument (passed from Java)
    if len(sys.argv) > 1:
        text = sys.argv[1]
        text_to_speech(text)
    else:
        print("No text provided for TTS.")