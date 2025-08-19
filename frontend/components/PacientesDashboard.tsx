import React, { useEffect, useState } from "react";
import PacienteFormModal from "./PacienteFormModal";

interface Paciente {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
}

const API_URL = "http://localhost:8080/api/pacientes";

const PacientesDashboard = () => {
  const [pacientes, setPacientes] = useState<Paciente[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [editPaciente, setEditPaciente] = useState<Paciente | null>(null);

  const fetchPacientes = () => {
    setLoading(true);
    fetch(API_URL)
      .then((res) => {
        if (!res.ok) throw new Error("Error al obtener pacientes");
        return res.json();
      })
      .then((data) => {
        setPacientes(Array.isArray(data) ? data : (data.content || []));
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchPacientes();
  }, []);

  const handleAdd = () => {
    setEditPaciente(null);
    setModalOpen(true);
  };

  const handleEdit = (paciente: Paciente) => {
    setEditPaciente(paciente);
    setModalOpen(true);
  };

  const handleDelete = (id: number) => {
    if (!window.confirm("¿Seguro que deseas eliminar este paciente?")) return;
    fetch(`${API_URL}/${id}`, { method: "DELETE" })
      .then((res) => {
        if (!res.ok) throw new Error("Error al eliminar paciente");
        fetchPacientes();
      })
      .catch((err) => setError(err.message));
  };

  const handleModalSubmit = (data: { nombre: string; apellido: string; email: string; telefono: string }) => {
    setError("");
    const method = editPaciente ? "PUT" : "POST";
    const url = editPaciente ? `${API_URL}/${editPaciente.id}` : API_URL;
    fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Error al guardar paciente");
        setModalOpen(false);
        setEditPaciente(null);
        fetchPacientes();
      })
      .catch((err) => setError(err.message));
  };

  return (
    <div className="p-8 bg-gradient-to-br from-blue-50 to-white min-h-screen">
      <header className="flex items-center justify-between mb-8">
        <div className="flex items-center gap-3">
          <span className="inline-flex items-center justify-center h-12 w-12 rounded-full bg-blue-100">
            <svg className="h-7 w-7 text-blue-600" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
              <circle cx="12" cy="12" r="10" />
              <path d="M16 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="9" r="4" />
            </svg>
          </span>
          <h1 className="text-3xl font-bold text-slate-800 tracking-tight">Pacientes</h1>
        </div>
        <button
          className="bg-gradient-to-r from-blue-600 to-blue-500 text-white font-semibold px-6 py-2.5 rounded-lg shadow-lg hover:from-blue-700 hover:to-blue-600 transition-all duration-200 flex items-center gap-2"
          onClick={handleAdd}
        >
          <svg className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" /></svg>
          Agregar Paciente
        </button>
      </header>
      {loading && <div className="text-slate-500 text-center py-8">Cargando pacientes...</div>}
      {error && <div className="text-red-500 mb-4 text-center">{error}</div>}
      {!loading && !error && (
        <div className="bg-white rounded-2xl shadow-xl border border-slate-200 overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200">
            <thead className="bg-blue-50 sticky top-0 z-10">
              <tr>
                <th className="py-3 px-6 text-left text-xs font-bold text-blue-700 uppercase tracking-wider">ID</th>
                <th className="py-3 px-6 text-left text-xs font-bold text-blue-700 uppercase tracking-wider">Nombre</th>
                <th className="py-3 px-6 text-left text-xs font-bold text-blue-700 uppercase tracking-wider">Apellido</th>
                <th className="py-3 px-6 text-left text-xs font-bold text-blue-700 uppercase tracking-wider">Email</th>
                <th className="py-3 px-6 text-left text-xs font-bold text-blue-700 uppercase tracking-wider">Teléfono</th>
                <th className="py-3 px-6 text-left text-xs font-bold text-blue-700 uppercase tracking-wider">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {pacientes.map((paciente) => (
                <tr key={paciente.id} className="hover:bg-blue-50 transition-colors">
                  <td className="py-3 px-6 font-mono text-slate-500">{paciente.id}</td>
                  <td className="py-3 px-6 font-semibold text-slate-800">{paciente.nombre}</td>
                  <td className="py-3 px-6 text-slate-700">{paciente.apellido}</td>
                  <td className="py-3 px-6 text-slate-700">{paciente.email}</td>
                  <td className="py-3 px-6 text-slate-700">{paciente.telefono}</td>
                  <td className="py-3 px-6 flex gap-2">
                    <button
                      className="inline-flex items-center gap-1 px-3 py-1.5 rounded-md bg-blue-100 text-blue-700 hover:bg-blue-200 transition text-xs font-semibold shadow-sm"
                      onClick={() => handleEdit(paciente)}
                      title="Editar"
                    >
                      <svg className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M15.232 5.232l3.536 3.536M9 13l6-6m2 2l-6 6m-2 2h2v2h2v-2h2v-2h-2v-2h-2v2H9v2z" /></svg>
                      Editar
                    </button>
                    <button
                      className="inline-flex items-center gap-1 px-3 py-1.5 rounded-md bg-red-100 text-red-700 hover:bg-red-200 transition text-xs font-semibold shadow-sm"
                      onClick={() => handleDelete(paciente.id)}
                      title="Eliminar"
                    >
                      <svg className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
                      Eliminar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      <PacienteFormModal
        open={modalOpen}
        onClose={() => {
          setModalOpen(false);
          setEditPaciente(null);
        }}
        onSubmit={handleModalSubmit}
        initialData={editPaciente ? { nombre: editPaciente.nombre, apellido: editPaciente.apellido, email: editPaciente.email, telefono: editPaciente.telefono } : undefined}
        isEdit={!!editPaciente}
      />
    </div>
  );
};

export default PacientesDashboard;
