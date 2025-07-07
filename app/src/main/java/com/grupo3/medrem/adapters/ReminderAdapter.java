package com.grupo3.medrem.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.grupo3.medrem.R;

import java.util.ArrayList;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    public static final int ESTADO_PENDIENTE = 0;
    public static final int ESTADO_TOMADO = 1;
    public static final int ESTADO_PERDIDO = 2;

    private List<ReminderItem> listaRecordatorios;
    private OnReminderActionListener listener;

    public interface OnReminderActionListener {
        void onConfirmReminder(int position);
        void onCancelReminder(int position);
    }

    public ReminderAdapter(List<ReminderItem> listaRecordatorios, OnReminderActionListener listener) {
        this.listaRecordatorios = listaRecordatorios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder_card, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        ReminderItem reminder = listaRecordatorios.get(position);

        holder.nombre_medicamento.setText(reminder.getNombre_medicamento());
        holder.texto_dosis.setText(reminder.getDosis());
        holder.texto_tiempo.setText(reminder.getTexto_tiempo());

        int backgroundColor;
        switch (reminder.getEstado()) {
            case ESTADO_TOMADO:
                backgroundColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorTaken);
                break;
            case ESTADO_PERDIDO:
                backgroundColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorMissed);
                break;
            default:
                backgroundColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPending);
                break;
        }
        holder.cardBackground.setBackgroundColor(backgroundColor);

        configureStatusIndicators(holder, reminder,position);
    }

    private void configureStatusIndicators(ReminderViewHolder holder, ReminderItem reminder, int position) {
        holder.icono_tomado.setVisibility(View.GONE);
        holder.texto_tomado.setVisibility(View.GONE);
        holder.icono_pedido.setVisibility(View.GONE);
        holder.text_perdido.setVisibility(View.GONE);
        holder.boton_confirmar.setVisibility(View.GONE);
        holder.boton_cancelar.setVisibility(View.GONE);

        switch (reminder.getEstado()) {
            case ESTADO_TOMADO:
                holder.icono_tomado.setVisibility(View.VISIBLE);
                holder.texto_tomado.setVisibility(View.VISIBLE);
                break;

            case ESTADO_PERDIDO:
                holder.icono_pedido.setVisibility(View.VISIBLE);
                holder.text_perdido.setVisibility(View.VISIBLE);
                break;

            case ESTADO_PENDIENTE:
                if (reminder.isEsDeHoy()) {
                    holder.boton_confirmar.setVisibility(View.VISIBLE);
                    holder.boton_cancelar.setVisibility(View.VISIBLE);

                    holder.boton_confirmar.setOnClickListener(v -> {
                        if (listener != null && position != RecyclerView.NO_POSITION) {
                            reminder.marcarComoTomado(holder.itemView.getContext());
                            listener.onConfirmReminder(position);
                        }
                    });

                    holder.boton_cancelar.setOnClickListener(v -> {
                        if (listener != null && position != RecyclerView.NO_POSITION) {
                            reminder.marcarComoPerdido(holder.itemView.getContext());
                            listener.onCancelReminder(position);
                        }
                    });
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listaRecordatorios.size();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView nombre_medicamento, texto_dosis, texto_tiempo, texto_tomado, text_perdido;
        ImageView icono_tomado, icono_pedido;
        ImageButton boton_confirmar, boton_cancelar;
        ConstraintLayout cardBackground;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre_medicamento = itemView.findViewById(R.id.nombreMedicamento);
            texto_dosis = itemView.findViewById(R.id.textoDosis);
            texto_tiempo = itemView.findViewById(R.id.textoTiempo);
            texto_tomado = itemView.findViewById(R.id.takenText);
            text_perdido = itemView.findViewById(R.id.missedText);
            icono_tomado = itemView.findViewById(R.id.takenIcon);
            icono_pedido = itemView.findViewById(R.id.missedIcon);
            boton_confirmar = itemView.findViewById(R.id.confirmButton);
            boton_cancelar = itemView.findViewById(R.id.cancelButton);
            cardBackground = itemView.findViewById(R.id.cardBackground);
        }
    }

    public static class ReminderItem {
        private int idRecordatorio;
        private String nombre_medicamento;
        private String dosis;
        private String texto_tiempo;
        private int estado;
        private boolean esDeHoy;


        public ReminderItem(int idRecordatorio, String nombre_medicamento, String dosis, String texto_tiempo, int estado,boolean esDeHoy) {
            this.idRecordatorio = idRecordatorio;
            this.nombre_medicamento = nombre_medicamento;
            this.dosis = dosis;
            this.texto_tiempo = texto_tiempo;
            this.estado = estado;
            this.esDeHoy = esDeHoy;
        }
        public int getIdRecordatorio() { return idRecordatorio; }
        public String getNombre_medicamento() { return nombre_medicamento; }
        public String getDosis() { return dosis; }
        public String getTexto_tiempo() { return texto_tiempo; }
        public int getEstado() { return estado; }

        public void setEstado(int estado) {
            this.estado = estado;
        }
        public boolean isEsDeHoy() {
            return esDeHoy;
        }
        public void setEsDeHoy(boolean esDeHoy) { this.esDeHoy = esDeHoy; }
        public void marcarComoTomado(Context context) {
            context.getSharedPreferences("recordatorio_estados", Context.MODE_PRIVATE)
                    .edit().putInt("estado_" + idRecordatorio, ESTADO_TOMADO).apply();
        }
        public void marcarComoPerdido(Context context) {
            context.getSharedPreferences("recordatorio_estados", Context.MODE_PRIVATE)
                    .edit().putInt("estado_" + idRecordatorio, ESTADO_PERDIDO).apply();
        }
        public void deshacerEstado(Context context) {
            context.getSharedPreferences("recordatorio_estados", Context.MODE_PRIVATE)
                    .edit().remove("estado_" + idRecordatorio).apply();
        }
        private void guardarEstado(Context context, int estado) {
            SharedPreferences prefs = context.getSharedPreferences("recordatorio_estados", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("estado_" + idRecordatorio, estado);
            editor.apply();
        }
    }
}