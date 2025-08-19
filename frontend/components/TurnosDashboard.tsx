import React, { useEffect, useState } from "react";
import TurnoFormModal from "./TurnoFormModal";

interface Turno {
  id: number;
  pacienteId: number;
  medicoId: number;
  fechaHora: string;
}

const API_URL = "http://localhost:8080/api/turnos";

const TurnosDashboard = () => {
  const [turnos, setTurnos] = useState<Turno[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [editTurno, setEditTurno] = useState<Turno | null>(null);

  const fetchTurnos = () => {
    setLoading(true);
    fetch(API_URL)
      .then((res) => {
        if (!res.ok) throw new Error("Error al obtener turnos");
        return res.json();
      })
      .then((data) => {
        setTurnos(Array.isArray(data) ? data : (data.content || []));
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchTurnos();
  }, []);

  const handleAdd = () => {
    setEditTurno(null);
    setModalOpen(true);
  };

  const handleEdit = (turno: Turno) => {
    setEditTurno(turno);
    setModalOpen(true);
  };

  const handleDelete = (id: number) => {
    if (!window.confirm("¿Seguro que deseas anular este turno?")) return;
    fetch(`${API_URL}/${id}`, { method: "DELETE" })
      .then((res) => {
        if (!res.ok) throw new Error("Error al anular turno");
        fetchTurnos();
      })
      .catch((err) => setError(err.message));
  };

  const handleModalSubmit = (data: { pacienteId: string; medicoId: string; fechaHora: string }) => {
    setError("");
    const method = editTurno ? "PUT" : "POST";
    const url = editTurno ? `${API_URL}/${editTurno.id}` : API_URL;
    fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ pacienteId: Number(data.pacienteId), medicoId: Number(data.medicoId), fechaHora: data.fechaHora }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Error al guardar turno");
        setModalOpen(false);
        setEditTurno(null);
        fetchTurnos();
      })
      .catch((err) => setError(err.message));
  };

  return (
    <div className="p-8 bg-gradient-to-br from-blue-50 to-white min-h-screen">
      <header className="flex items-center justify-between mb-8">
        <div className="flex items-center gap-3">
          <span className="inline-flex items-center justify-center h-12 w-12 rounded-full bg-blue-100">
            <svg className="h-7 w-7 text-blue-600" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
              <rect x="4" y="4" width="16" height="16" rx="2" />
              <path d="M8 2v4M16 2v4M4 10h16" />
            </svg>
          </span>
          <h1 className="text-3xl font-bold text-slate-800 tracking-tight">Turnos</h1>
        </div>
        <button
          className="bg-gradient-to-r from-blue-600 to-blue-500 text-white font-semibold px-6 py-2.5 rounded-lg shadow-lg hover:from-blue-700 hover:to-blue-600 transition-all duration-200 flex items-center gap-2"
          onClick={handleAdd}
        >
          <svg className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" /></svg>
          Agendar Turno
        </button>
      </header>
      {loading && <div className="text-slate-500 text-center py-8">Cargando turnos...</div>}
      {error && <div className="text-red-500 mb-4 text-center">{error}</div>}
      {!loading && !error && (
        <div className="bg-white rounded-2xl shadow-xl border border-slate-200 overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200">
            <thead className="bg-blue-50 sticky top-0 z-10">
              <tr>
                <th className="py-3 px-6 text-left text-xs font-bold text-blue-700 uppercase tracking-wider">ID</th>
                <th className="py-3 px-6 text-left text-xs font-bold text-blue-700 uppercase tracking-wider">Paciente ID</th>
                <th className="py-3 px-6 text-left text-xs font-bold text-blue-700 uppercase tracking-wider">Médico ID</th>
                <th className="py-3 px-6 text-left text-xs font-bold text-blue-700 uppercase tracking-wider">Fecha y Hora</th>
                <th className="py-3 px-6 text-left text-xs font-bold text-blue-700 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {turnos.map((turno) => (
                <tr key={turno.id} className="hover:bg-blue-50 transition-colors">
                  <td className="py-3 px-6 font-mono text-slate-500">{turno.id}</td>
                  <td className="py-3 px-6 text-slate-700">{turno.pacienteId}</td>
                  <td className="py-3 px-6 text-slate-700">{turno.medicoId}</td>
                  <td className="py-3 px-6 text-slate-700">{new Date(turno.fechaHora).toLocaleString()}</td>
                  <td className="py-3 px-6 flex gap-2">
                    <button
                      className="inline-flex items-center gap-1 px-3 py-1.5 rounded-md bg-blue-100 text-blue-700 hover:bg-blue-200 transition text-xs font-semibold shadow-sm"
                      onClick={() => handleEdit(turno)}
                      title="Modificar"
                    >
                      <svg className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M15.232 5.232l3.536 3.536M9 13l6-6m2 2l-6 6m-2 2h2v2h2v-2h2v-2h-2v-2h-2v2H9v2z" /></svg>
                      Modificar
                    </button>
                    <button
                      className="inline-flex items-center gap-1 px-3 py-1.5 rounded-md bg-red-100 text-red-700 hover:bg-red-200 transition text-xs font-semibold shadow-sm"
                      onClick={() => handleDelete(turno.id)}
                      title="Anular"
                    >
                      <svg className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
                      Anular
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      <TurnoFormModal
        open={modalOpen}
        onClose={() => {
          setModalOpen(false);
          setEditTurno(null);
        }}
        onSubmit={handleModalSubmit}
        initialData={editTurno ? { pacienteId: String(editTurno.pacienteId), medicoId: String(editTurno.medicoId), fechaHora: editTurno.fechaHora } : undefined}
        isEdit={!!editTurno}
      />
    </div>
  );
};

export default TurnosDashboard;
