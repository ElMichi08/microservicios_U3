import React, { useEffect, useState } from "react";

interface Notificacion {
  id: number;
  pacienteId: number;
  email: string;
  telefono: string;
  mensaje: string;
  fechaHora: string;
  tipo: string;
}

const API_URL = "http://localhost:8080/api/notificaciones";

const tipoColor: Record<string, string> = {
  CANCELACION: "bg-red-100 text-red-700 border-red-300",
  CONFIRMACION: "bg-green-100 text-green-700 border-green-300",
  VIGENTE: "bg-blue-100 text-blue-700 border-blue-300",
};

const NotificacionesDashboard = () => {
  const [notificaciones, setNotificaciones] = useState<Notificacion[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetch(API_URL)
      .then((res) => res.json())
      .then((data) => {
        setNotificaciones(Array.isArray(data) ? data : (data.content || []));
        setLoading(false);
      })
      .catch((err) => {
        setError("Error al cargar notificaciones");
        setLoading(false);
      });
  }, []);

  return (
    <div className="p-8 bg-gradient-to-br from-blue-50 to-white min-h-screen">
      <header className="flex items-center gap-3 mb-8">
        <span className="inline-flex items-center justify-center h-12 w-12 rounded-full bg-blue-100">
          <svg className="h-7 w-7 text-blue-600" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
            <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9" />
            <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0" />
          </svg>
        </span>
        <h1 className="text-3xl font-bold text-slate-800 tracking-tight">Notificaciones</h1>
      </header>
      {loading && <div className="text-slate-500 text-center py-8">Cargando notificaciones...</div>}
      {error && <div className="text-red-500 mb-4 text-center">{error}</div>}
      {!loading && !error && notificaciones.length === 0 && (
        <div className="text-slate-500 text-center py-8">No hay notificaciones.</div>
      )}
      <div className="grid gap-4 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
        {notificaciones.map((n) => (
          <div
            key={n.id}
            className={`border rounded-xl p-5 shadow-sm flex flex-col gap-2 ${tipoColor[n.tipo] || "bg-slate-100 text-slate-700 border-slate-200"}`}
          >
            <div className="flex items-center gap-2">
              <span className="font-bold">{n.tipo === "CANCELACION" ? "Cancelación" : n.tipo === "CONFIRMACION" ? "Confirmación" : "Vigente"}</span>
              <span className="text-xs text-slate-400">{new Date(n.fechaHora).toLocaleString()}</span>
            </div>
            <div className="text-base">{n.mensaje}</div>
            <div className="text-xs text-slate-500">Paciente ID: {n.pacienteId}</div>
            <div className="flex gap-2 text-xs">
              <span>Email: {n.email}</span>
              <span>Tel: {n.telefono}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default NotificacionesDashboard;
