package com.arruadev.wolbutton;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String PREFS = "wol_settings";

    private EditText macInput;
    private EditText broadcastInput;
    private EditText portInput;
    private TextView statusText;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        macInput = findViewById(R.id.macInput);
        broadcastInput = findViewById(R.id.broadcastInput);
        portInput = findViewById(R.id.portInput);
        statusText = findViewById(R.id.statusText);
        Button wakeButton = findViewById(R.id.wakeButton);

        loadSettings();
        wakeButton.setOnClickListener(view -> sendWakePacket());
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        macInput.setText(prefs.getString("mac", "AA:BB:CC:DD:EE:FF"));
        broadcastInput.setText(prefs.getString("broadcast", "192.168.0.255"));
        portInput.setText(prefs.getString("port", "9"));
    }

    private void saveSettings(String mac, String broadcast, String port) {
        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit()
                .putString("mac", mac)
                .putString("broadcast", broadcast)
                .putString("port", port)
                .apply();
    }

    private void sendWakePacket() {
        String mac = macInput.getText().toString().trim();
        String broadcast = broadcastInput.getText().toString().trim();
        String portText = portInput.getText().toString().trim();

        saveSettings(mac, broadcast, portText);
        statusText.setText("Enviando pacote...");

        new Thread(() -> {
            try {
                int port = Integer.parseInt(portText);
                byte[] macBytes = parseMac(mac);
                byte[] packetBytes = buildMagicPacket(macBytes);
                InetAddress address = InetAddress.getByName(broadcast);

                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.setBroadcast(true);
                    DatagramPacket packet = new DatagramPacket(
                            packetBytes,
                            packetBytes.length,
                            address,
                            port
                    );

                    for (int i = 0; i < 3; i++) {
                        socket.send(packet);
                        Thread.sleep(120);
                    }
                }

                postStatus(String.format(Locale.US, "Pacote enviado para %s:%d", broadcast, port));
            } catch (Exception error) {
                postStatus("Erro: " + error.getMessage());
            }
        }).start();
    }

    private static byte[] parseMac(String mac) {
        String clean = mac.replace("-", "").replace(":", "").trim();
        if (clean.length() != 12) {
            throw new IllegalArgumentException("MAC precisa ter 12 caracteres hexadecimais.");
        }

        byte[] result = new byte[6];
        for (int i = 0; i < 6; i++) {
            String part = clean.substring(i * 2, i * 2 + 2);
            result[i] = (byte) Integer.parseInt(part, 16);
        }
        return result;
    }

    private static byte[] buildMagicPacket(byte[] macBytes) {
        byte[] bytes = new byte[6 + 16 * macBytes.length];
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) 0xFF;
        }
        for (int i = 6; i < bytes.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
        }
        return bytes;
    }

    private void postStatus(String message) {
        mainHandler.post(() -> statusText.setText(message));
    }
}
