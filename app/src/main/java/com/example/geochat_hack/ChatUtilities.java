package com.example.geochat_hack;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

public class ChatUtilities {
    public static Spanned FormatMessage (String username, String message) {
        Spanned formattedString = Html.fromHtml("<font color='#197d02'><b>" + username + ": </b></font>" + message);
        return formattedString;
    }

    public static TextView GenerateTextView (Context context, String[] messageDetails) {
        // messageDetails[0] = username
        // messageDetails[1] = message
        TextView generatedTextView = new TextView(context);
        generatedTextView.setText(FormatMessage(messageDetails[0], messageDetails[1]));
        return generatedTextView;
    }

}
