#!/usr/bin/env python3
"""Servidor local mínimo para demostrar Mobile -> TV.

Ejecutar desde la carpeta del proyecto:
    py sync_server.py

Los emuladores Android acceden a esta computadora mediante http://10.0.2.2:8765/
No requiere instalar Flask, FastAPI ni ninguna otra librería.
"""

from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
import json
import threading

HOST = "0.0.0.0"
PORT = 8765

state = {"dishId": None, "version": 0}
lock = threading.Lock()


class Handler(BaseHTTPRequestHandler):
    def _send_json(self, status, payload):
        body = json.dumps(payload, ensure_ascii=False).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def do_GET(self):
        if self.path != "/selection":
            self._send_json(404, {"error": "Ruta no encontrada"})
            return

        with lock:
            response = dict(state)
        self._send_json(200, response)

    def do_POST(self):
        if self.path != "/selection":
            self._send_json(404, {"error": "Ruta no encontrada"})
            return

        try:
            content_length = int(self.headers.get("Content-Length", "0"))
            raw = self.rfile.read(content_length)
            data = json.loads(raw.decode("utf-8"))
            dish_id = data.get("dishId")

            if not isinstance(dish_id, str) or not dish_id.strip():
                self._send_json(400, {"error": "dishId es obligatorio"})
                return

            with lock:
                state["dishId"] = dish_id.strip()
                state["version"] += 1
                response = {
                    "ok": True,
                    "dishId": state["dishId"],
                    "version": state["version"],
                }

            print(f"Selección recibida: {response['dishId']} (versión {response['version']})")
            self._send_json(200, response)
        except (ValueError, json.JSONDecodeError):
            self._send_json(400, {"error": "JSON inválido"})

    def log_message(self, fmt, *args):
        print("[sync] " + (fmt % args))


if __name__ == "__main__":
    server = ThreadingHTTPServer((HOST, PORT), Handler)
    print(f"Servidor de sincronización activo en http://localhost:{PORT}")
    print("Déjalo abierto mientras pruebas Mobile y TV. Ctrl+C para detenerlo.")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nServidor detenido.")
    finally:
        server.server_close()
