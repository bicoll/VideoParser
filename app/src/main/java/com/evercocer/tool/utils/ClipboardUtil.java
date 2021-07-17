package com.evercocer.tool.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.annotation.Nullable;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ClipboardUtil {

    private ClipboardUtil() {
    }

    private static class Holder {
        private final static ClipboardUtil CLIPBOARD_UTIL = new ClipboardUtil();

    }


    public static ClipboardUtil getInstance() {
        return Holder.CLIPBOARD_UTIL;
    }

    /**
     * 获取剪切板最新内容
     *
     * @param context 上下文
     */

    /**
     * 获取剪切板上的内容
     */
    @Nullable
    public String getClipboardContent(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            ClipData data = clipboardManager.getPrimaryClip();
            if (data != null && data.getItemCount() > 0) {
                ClipData.Item item = data.getItemAt(0);
                if (item != null) {
                    String content = item.getText().toString();
                    return content;
                }
            }
        }
        return "null";
    }

    /**
     * 复制文字到剪切板
     *
     * @param context 上下文
     * @param text    文本内容
     */
    public void setPrimaryClip(Context context, String text) {
        ClipboardManager myClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
    }
}
