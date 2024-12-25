import os
import time
import traceback
from datetime import datetime
from multiprocessing import Manager, Process, active_children

from bottle import error, get, request, route, run

import clear


def get_date():
    """Get the current date
    Returns:
        date string
    """
    return time.strftime("%Y-%m-%d", time.localtime(time.time()))


class ap_info:
    """Define the WAP information object"""

    def __init__(self):
        self.uuid = ""
        self.bssid = ""
        self.ssid = ""
        self.fp_id = ""
        self.datetime = ""
        self.sdk = ""
        self.cap = ""
        self.locx = ""
        self.locy = ""
        self.locz = ""
        self.locb = ""
        self.locf = ""
        self.locr = ""
        self.device = ""
        self.cf0 = ""
        self.cf1 = ""
        self.cwidth = ""
        self.freq = ""
        self.level = ""
        self.timestamp = ""
        self.accx = ""
        self.accy = ""
        self.accz = ""
        self.grox = ""
        self.groy = ""
        self.groz = ""
        self.magx = ""
        self.magy = ""
        self.magz = ""
        self.fp_uuid = ""
        self.fp_name = ""
        self.nonce = ""

    def check(self):
        if self.ssid == "":
            self.ssid = "no_ssid"
        if self.locb == "":
            self.locb = "no_locb"
        if self.locr == "":
            self.locr = "no_locr"


class tweb(Process):
    """Thread to handle the web server"""

    def __init__(self, queue):
        super().__init__()
        self.queue = queue

    def run(self):
        """Run the web server"""

        @error(404)
        def error404(error):
            """No such function"""
            return "404 - No this fun"

        @route("/hello")
        def hello():
            """Hello world testing"""
            return time.ctime() + "\n Hello.\n"

        @get("/order")
        def empty():
            """Empty the database"""
            code = request.query.code
            print("Server get:\b" + code)
            if code == "666":
                clear.do_clean()
                return "Function do_clean() activated."
            return "Get error code."

        @get("/msg")
        def load():
            """Load the message from the queue"""
            ap = ap_info()
            ap.bssid = request.query.bssid
            ap.ssid = request.query.ssid
            ap.datetime = request.query.datetime
            ap.sdk = request.query.sdk
            ap.device = request.query.device
            ap.timestamp = request.query.timestamp
            ap.cf0 = request.query.cf0
            ap.cf1 = request.query.cf1
            ap.cwidth = request.query.cw
            ap.level = request.query.level
            ap.freq = request.query.freq
            ap.locb = request.query.locb
            ap.locf = request.query.locf
            ap.locr = request.query.locr
            ap.locx = request.query.locx
            ap.locy = request.query.locy
            ap.locz = request.query.locz
            ap.accx = request.query.accx
            ap.accz = request.query.accz
            ap.accy = request.query.accy
            ap.grox = request.query.grox
            ap.groy = request.query.groy
            ap.groz = request.query.groz
            ap.magx = request.query.magx
            ap.magy = request.query.magy
            ap.magz = request.query.magz
            # Write to log file
            fp = "./raw_output/" + ap.locb + "/" + ap.locf + "/" + ap.locz + "/"
            os.makedirs(
                fp,
                exist_ok=True,
            )
            try:
                # Write to the file
                with open(
                    fp + "{0}.csv".format(datetime.now().strftime("%Y%m%d%H%M")), "a+"
                ) as f:
                    f.write(
                        ap.bssid
                        + ","
                        + ap.device
                        + ","
                        + ap.freq
                        + ","
                        + ap.level
                        + ","
                        + ap.locx
                        + ","
                        + ap.locy
                        + ","
                        + ap.accx
                        + ","
                        + ap.accy
                        + ","
                        + ap.accz
                        + ","
                        + ap.grox
                        + ","
                        + ap.groy
                        + ","
                        + ap.groz
                        + ","
                        + ap.magx
                        + ","
                        + ap.magy
                        + ","
                        + ap.magz
                        + "\n"
                    )
                    f.close()
            except:
                print("IO exception")
                traceback.print_exc()
                print(traceback.format_exc())
            # Write to queue
            self.queue.put(ap)
            return "Message received and processed."

        try:
            run(host="0.0.0.0", port=5001, debug=True)
        except:
            print("traceback_exc():")
            traceback.print_exc()
            print(traceback.format_exc())


if __name__ == "__main__":
    que = Manager().Queue()
    process_rec_to_log = tweb(que)
    process_rec_to_log.start()
    print(active_children())
    process_rec_to_log.join()
    print("Execution finished...")
