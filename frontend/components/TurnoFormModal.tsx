import React from "react";

interface Paciente {
  id: number;
  nombre: string;
  apellido: string;
}
interface Medico {
  id: number;
  nombre: string;
  especialidad: string;
}
interface TurnoFormModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: { pacienteId: string; medicoId: string; fechaHora: string }) => void;
  initialData?: { pacienteId: string; medicoId: string; fechaHora: string };
  isEdit?: boolean;
}

const TurnoFormModal: React.FC<TurnoFormModalProps> = ({ open, onClose, onSubmit, initialData, isEdit }) => {
  const [pacienteId, setPacienteId] = React.useState(initialData?.pacienteId || "");
  const [medicoId, setMedicoId] = React.useState(initialData?.medicoId || "");
  const [fechaHora, setFechaHora] = React.useState(initialData?.fechaHora || "");
  const [pacientes, setPacientes] = React.useState<Paciente[]>([]);
  const [medicos, setMedicos] = React.useState<Medico[]>([]);

  React.useEffect(() => {
    setPacienteId(initialData?.pacienteId || "");
    setMedicoId(initialData?.medicoId || "");
    setFechaHora(initialData?.fechaHora || "");
  }, [initialData, open]);

  React.useEffect(() => {
    if (open) {
      fetch("http://localhost:8080/api/pacientes")
        .then((res) => res.json())
        .then((data) => setPacientes(Array.isArray(data) ? data : (data.content || [])));
      fetch("http://localhost:8080/api/medicos")
        .then((res) => res.json())
        .then((data) => setMedicos(Array.isArray(data) ? data : (data.content || [])));
    }
  }, [open]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({ pacienteId, medicoId, fechaHora });
  };

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-30">
      <div className="bg-white rounded-lg shadow-lg w-full max-w-md p-6 relative">
        <button
          className="absolute top-2 right-2 text-gray-400 hover:text-gray-600 text-xl"
          onClick={onClose}
        >
          ×
        </button>
        <h2 className="text-2xl font-bold mb-4 text-slate-800">{isEdit ? "Modificar Turno" : "Agendar Turno"}</h2>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <select
            value={pacienteId}
            onChange={(e) => setPacienteId(e.target.value)}
            className="border border-slate-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-200"
            required
          >
            <option value="">Selecciona un paciente</option>
            {pacientes.map((p) => (
              <option key={p.id} value={p.id}>{p.nombre} {p.apellido}</option>
            ))}
          </select>
          <select
            value={medicoId}
            onChange={(e) => setMedicoId(e.target.value)}
            className="border border-slate-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-200"
            required
          >
            <option value="">Selecciona un médico</option>
            {medicos.map((m) => (
              <option key={m.id} value={m.id}>{m.nombre} ({m.especialidad})</option>
            ))}
          </select>
          <input
            type="datetime-local"
            placeholder="Fecha y Hora"
            value={fechaHora}
            onChange={(e) => setFechaHora(e.target.value)}
            className="border border-slate-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-200"
            required
          />
          <div className="flex justify-end gap-2 mt-2">
            <button
              type="button"
              className="px-4 py-2 rounded bg-gray-100 text-gray-700 hover:bg-gray-200"
              onClick={onClose}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded bg-blue-600 text-white font-semibold hover:bg-blue-700 shadow-sm"
            >
              {isEdit ? "Actualizar" : "Agendar"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default TurnoFormModal;
