#!/bin/sh
set -eu

export DISPLAY=:0
Xvfb :0 -screen 0 1280x800x24 -nolisten tcp &
XVFB_PID=$!

attempt=0
until xdpyinfo -display "$DISPLAY" >/dev/null 2>&1; do
    if ! kill -0 "$XVFB_PID" 2>/dev/null; then
        wait "$XVFB_PID"
        exit 1
    fi
    attempt=$((attempt + 1))
    if [ "$attempt" -ge 30 ]; then
        echo "L'écran virtuel Xvfb n'a pas démarré à temps." >&2
        exit 1
    fi
    sleep 1
done

fluxbox &
x11vnc -display :0 -forever -shared -nopw -listen 127.0.0.1 -rfbport 5900 &
websockify --web=/usr/share/novnc 0.0.0.0:6080 127.0.0.1:5900 &

exec java -cp "bin:lib/ojdbc17.jar" Aff.Main
