package com.yang.guessmusic.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

import com.yang.guessmusic.data.Const;

public class FileStorage {
    public static void saveGameInfo(Context context, int level, int coins) {
        FileOutputStream fos = null;
        DataOutputStream dos = null;
        try {
            fos = context.openFileOutput(Const.FILE_GAME_INFO, Context.MODE_PRIVATE);
            dos = new DataOutputStream(fos);
            dos.writeInt(level);
            dos.writeInt(coins);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int[] loadGameInfo(Context context) {
        FileInputStream fis = null;
        DataInputStream dis = null;
        int[] gameInfo = {-1, Const.TOTAL_COINS};
        try {
            fis = context.openFileInput(Const.FILE_GAME_INFO);
            dis = new DataInputStream(fis);
            gameInfo[Const.LOAD_GAME_INFO_LEVEL] = dis.readInt();
            gameInfo[Const.LOAD_GAME_INFO_COINS] = dis.readInt();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return gameInfo;
    }
}
