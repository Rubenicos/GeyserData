package com.saicone.geyserdata.connection;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.session.GeyserSession;

import java.util.ArrayList;
import java.util.List;

public abstract class DownstreamConnection<T> {

    final T player;
    final byte[] data;

    DownstreamConnection(T player, byte[] data) {
        this.player = player;
        this.data = data;
    }

    protected abstract String getPlayerName();

    protected abstract List<String> getPlayerList(String server);

    protected void sendResponse(byte[] data) {
    }

    public void process() {
        ByteArrayDataInput in = ByteStreams.newDataInput(data);

        String channel = in.readUTF();
        switch (channel) {
            case "Platform":
                processPlatform();
                break;
            case "PlatformOther":
                processPlatformOther(in.readUTF());
                break;
            case "PlayerCount":
                processPlayerCount(in.readUTF());
                break;
            case "PlayerList":
                processPlayerList(in.readUTF());
                break;
            default:
                break;
        }
    }

    protected void processPlatform() {
        if (player == null) {
            return;
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Platform");
        out.writeUTF(isBedrockPlayer(getPlayerName()) ? "BEDROCK" : "JAVA");

        sendResponse(out.toByteArray());
    }

    protected void processPlatformOther(String name) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlatformOther");
        out.writeUTF(name);
        out.writeUTF(isBedrockPlayer(name) ? "BEDROCK" : "JAVA");

        sendResponse(out.toByteArray());
    }

    protected void processPlayerCount(String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerCount");
        out.writeUTF(server);
        out.writeInt(filterBedrockPlayers(getPlayerList(server)).size());

        sendResponse(out.toByteArray());
    }

    protected void processPlayerList(String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerList");
        out.writeUTF(server);
        String players = String.join(" ", filterBedrockPlayers(getPlayerList(server)));
        if (players.isEmpty()) {
            players = " ";
        }
        out.writeUTF(players);

        sendResponse(out.toByteArray());
    }

    public boolean isBedrockPlayer(String name) {
        for (GeyserSession session : GeyserImpl.getInstance().getSessionManager().getAllSessions()) {
            if (session.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public List<String> filterBedrockPlayers(List<String> names) {
        List<String> players = new ArrayList<>();
        for (GeyserSession session : GeyserImpl.getInstance().getSessionManager().getAllSessions()) {
            if (names.contains(session.name())) {
                players.add(session.name());
            }
        }
        return players;
    }
}
