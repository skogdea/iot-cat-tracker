import os
from dotenv import load_dotenv
from gpiozero import RGBLED, MotionSensor
from datetime import datetime
import requests
import random
from flask import Flask, jsonify
import logging
import threading

from config.logging_config import setup_logging


setup_logging()
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)


# Look for an .env file in the current directory, load the variables from it into the system environment:
# Call this before try to access to the Environment Variable:
load_dotenv()

# Try to get the URL of the Spring Boot server endpoint from the Environment Variable to receive signal:
backend_url = os.getenv("BACKEND_URL")
if not backend_url:
    raise ValueError("BACKEND_URL environment variable is not set")


# Flask app initialization:
app = Flask(__name__)

# Initialize LED, PIR:
rgb = RGBLED(red=18, green=19, blue=27)
pir = MotionSensor(17)

# State variable:
monitoring = False


def send_signal_to_server():
    # Data to be sent (signal):
    payload = {
        "signal_id": random.randint(1, 9223372036854775807),
        "sensor_id": "1",
        "location": "bedroom",
        "timestamp": int(datetime.now().timestamp() * 1000),
    }

    try:
        # Send POST request with data as JSON:
        response = requests.post(backend_url, json=payload, timeout=5)

        if response.status_code == 200:
            logger.info(
                f"Signal sent to server successfully: {payload}, Server Response: {response.text}"
            )
        else:
            logger.info(
                f"Failed to send signal to server. HTTP Status Code: {response.status_code}"
            )

    except requests.exceptions.Timeout:
        logger.warning("Request timeout, server didn't respond.")
    except Exception as e:
        logger.error(f"Error occurred while sending signal to server: {e}")


def motion_detected():
    rgb.color = (1, 0, 0)  # Red
    timestamp = datetime.now().isoformat(timespec="milliseconds") + "Z"
    logger.info(f"Motion detected at: {timestamp}!")
    send_signal_to_server()


def no_motion_detected():
    rgb.color = (0, 0, 1)  # Blue


# Flask routes:
@app.route("/start-monitoring", methods=["POST"])
def start_monitoring():
    is_daemon = threading.current_thread().daemon
    which_thread = threading.current_thread().name
    logger.debug(f"Current thread Daemon: {is_daemon}")
    logger.debug(f"Current thread named: {which_thread}")

    global monitoring
    logger.debug(f"Current monitoring status: {monitoring}")
    if not monitoring:
        monitoring = True
        pir.when_motion = motion_detected
        pir.when_no_motion = no_motion_detected
        logger.info("Monitoring started.")
        return jsonify({"message": "Monitoring started"}), 200
    else:
        return jsonify({"message": "Monitoring already running"}), 400


@app.route("/stop-monitoring", methods=["POST"])
def stop_monitoring():
    is_daemon = threading.current_thread().daemon
    which_thread = threading.current_thread().name
    logger.debug(f"Current thread Daemon: {is_daemon}")
    logger.debug(f"Current thread named: {which_thread}")

    global monitoring
    logger.debug(f"Current monitoring status: {monitoring}")
    if monitoring:
        monitoring = False
        pir.when_motion = None  # Disable callbacks
        pir.when_no_motion = None
        logger.info("Monitoring stopped.")
        return jsonify({"message": "Monitoring stopped"}), 200
    else:
        return jsonify({"message": "Monitoring not running"}), 400


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=False, threaded=False)
