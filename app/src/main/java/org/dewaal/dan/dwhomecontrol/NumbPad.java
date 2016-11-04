package org.dewaal.dan.dwhomecontrol;

/**
 * Retrieved from http://www.tomswebdesign.net/Articles/Android/number-pad-input-class.html
 */

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class NumbPad {
    private boolean decimal = false;

    // FLAG VALUES
    //   flags can be combined like this: NumbPad.CURRENCY + NumbPad.ALLOW_NEGATIVE
    //   when passing flags to NumbPad.show();

    // no flags
    public static int NOFLAGS = 0;
    // hides the input with stars *
    public static int HIDE_INPUT = 1;
    // hides the prompt text
    public static int HIDE_PROMPT = 2;
    // hides the decimal button
    public static int NO_DECIMAL = 4;
    // displays the value as currency and enters value as 2 decimal value
    public static int CURRENCY = 8;
    // shows the negative button
    public static int ALLOW_NEGATIVE = 16;
    // sets the negative button
    public static int SET_NEGATIVE = 32;
    // accepts leading zeros
    public static int TEXT = 64;

    static Float amountDue;

    static TextView prompt;
    static TextView promptValue;

    static Button btn1;
    static Button btn2;
    static Button btn3;
    static Button btn4;
    static Button btn5;
    static Button btn6;
    static Button btn7;
    static Button btn8;
    static Button btn9;
    static Button btn0;
    static Button btnC;
    static Button btnDot;
    static CheckBox btnNegative;

    private String value = "";
    private String addl_text = "";
    private NumbPad me;

    private int flag_hideInput = 0;
    private int flag_hidePrompt = 0;
    private int flag_noDecimal = 0;
    private int flag_currency = 0;
    private int flag_allownegative = 0;
    private int flag_setnegative = 0;
    private int flag_text = 0;

    public interface numbPadInterface {
        public String numPadInputValue(String value);
        public String numPadCanceled();
    }

    public String getValue() {
        return value;
    }

    public void setAdditionalText(String inTxt) {
        addl_text = inTxt;
    }

    public void show(final Activity a, final String promptString, int inFlags,
                     final numbPadInterface postrun) {
        me = this;
        flag_hideInput = inFlags % 2;
        flag_hidePrompt = (inFlags / 2) % 2;
        flag_noDecimal = (inFlags / 4) % 2;
        flag_currency = (inFlags / 8) % 2;
        flag_allownegative = (inFlags / 16) % 2;
        flag_setnegative = (inFlags / 32) % 2;
        flag_text = (inFlags / 64) % 2;

        // special flag overrides
        if (flag_currency == 1) { flag_noDecimal = 1;}

        Builder dlg = new AlertDialog.Builder(a);
        if (flag_hidePrompt == 0) {
            dlg.setTitle(promptString);
        }
        // Inflate the dialog layout
        LayoutInflater inflater = a.getLayoutInflater();
        View iView = inflater.inflate(R.layout.numb_pad, null, false);

        // create code to handle the change tender
        prompt = (TextView) iView.findViewById(R.id.promptText);
        prompt.setText(addl_text);
        if (addl_text.equals("")) {
            prompt.setVisibility(View.GONE);
        }
        promptValue = (TextView) iView.findViewById(R.id.promptValue);

        // Defaults
        value = "";
        promptValue.setText("");

        // Buttons
        btn1 = (Button) iView.findViewById(R.id.button1);
        btn2 = (Button) iView.findViewById(R.id.button2);
        btn3 = (Button) iView.findViewById(R.id.button3);
        btn4 = (Button) iView.findViewById(R.id.button4);
        btn5 = (Button) iView.findViewById(R.id.button5);
        btn6 = (Button) iView.findViewById(R.id.button6);
        btn7 = (Button) iView.findViewById(R.id.button7);
        btn8 = (Button) iView.findViewById(R.id.button8);
        btn9 = (Button) iView.findViewById(R.id.button9);
        btn0 = (Button) iView.findViewById(R.id.button0);
        btnC = (Button) iView.findViewById(R.id.buttonC);
        btnDot = (Button) iView.findViewById(R.id.buttonDot);
        btnNegative = (CheckBox) iView.findViewById(R.id.negativeValue);

        // NO_DECIMAL flag
        if (flag_noDecimal == 1) {
//            btnDot.setText("00");
            btnDot.setVisibility(Button.INVISIBLE);
        }

        // ALLOW_NEGATIVE flag
        if (flag_allownegative == 1) {
            btnNegative.setVisibility(View.VISIBLE);
        }

        // SET NEGATIVE flag
        if (flag_setnegative == 1) {
            btnNegative.setChecked(true);
        }

        // Button listeners
        btnC.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                value = "";
                promptValue.setText("");
            }
        });
        btn1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("1");
            }
        });
        btn2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("2");
            }
        });
        btn3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("3");
            }
        });
        btn4.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("4");
            }
        });
        btn5.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("5");
            }
        });
        btn6.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("6");
            }
        });
        btn7.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("7");
            }
        });
        btn8.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("8");
            }
        });
        btn9.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("9");
            }
        });
        btn0.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                appendNumber("0");
            }
        });
        btnDot.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (flag_noDecimal == 1) {
                    appendNumber("00");
                } else {
                    appendNumber(".");
                }
            }
        });

        // set up the dialog
        dlg.setView(iView);
        dlg.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                dlg.dismiss();
                if (me.getValue().equals("")) {
                    // treat null value the same as canceled
                    postrun.numPadCanceled();
                } else {
                    if (flag_allownegative == 1) {
                        if (btnNegative.isChecked()) {
                            postrun.numPadInputValue("-" + me.getValue());
                        } else {
                            postrun.numPadInputValue(me.getValue());
                        }
                    } else {
                        postrun.numPadInputValue(me.getValue());
                    }
                }
            }
        });
        dlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                dlg.dismiss();
                postrun.numPadCanceled();
            }
        });
        // show the dialog
        dlg.show();
    }

    // Internal function to append the clicked number
    void appendNumber(String inNumb) {
        if (flag_text == 1) {
            value = value + inNumb;
            promptValue.setText(value);
        } else if (flag_hideInput == 1) {
            value = value + inNumb;
            promptValue.setText(promptValue.getText() + "*");
        } else {
            //if (value.equals("")) { value = "0";}
            float val;
            try {
                val = Float.parseFloat(value);
            } catch (NumberFormatException e) {
                val = 0f;
            }
            DecimalFormat df;
            if (flag_currency == 1) {
                df = new DecimalFormat("$0.00");
                if (inNumb.equals("00")) {
                    val = val * 10000 + Float.parseFloat(inNumb);
                } else {
                    val = val * 1000 + Float.parseFloat(inNumb);
                }
                val = val / 100;
                value = "" + val;
            } else {
                if (flag_noDecimal == 1) {
                    df = new DecimalFormat("#");
                } else {
                    df = new DecimalFormat("#.##");
                }
                if (value.equals("0")) { value = inNumb; }
                else { value = value + inNumb; }

                // handle decimal point
                if (inNumb.equals(".")) {
                    decimal = true;
                    promptValue.setText(df.format(val) + ".");
                    return;
                }
                if (decimal) {
                    decimal = false;
                }

                try {
                    val = Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    // do nothing if we can't parse
                }
            }
            promptValue.setText(df.format(val));
        }
    }
}