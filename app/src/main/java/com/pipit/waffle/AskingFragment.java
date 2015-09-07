package com.pipit.waffle;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pipit.waffle.Objects.Choice;
import com.pipit.waffle.Objects.Question;
import com.pipit.waffle.Objects.Self;
import com.pipit.waffle.Speech.SpeechToText;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Eric on 4/25/2015.
 */
public class AskingFragment extends Fragment {
    private ImageButton btnSpeak;
    private Button btnSubmit;
    private EditText textInputSpeechTop;
    private EditText textInputSpeechBot;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private LinearLayout holder_layout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((ToolbarActivity) getActivity()).current_fragment_id = Constants.ANSWERING_FRAGMENT_ID;
        View v = inflater.inflate(R.layout.asking_fragment, container, false);
        btnSpeak = (ImageButton) v.findViewById(R.id.btnSpeak);
        btnSubmit = (Button) v.findViewById(R.id.submit);
        textInputSpeechTop = (EditText) v.findViewById(R.id.txtSpeechInputTop);
        textInputSpeechBot = (EditText) v.findViewById(R.id.txtSpeechInputBot);
        final ProgressBar uploadProgress = (ProgressBar) v.findViewById(R.id.uploadprogress);

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String inputTextTop = textInputSpeechTop.getText().toString();
                String inputTextBot =  textInputSpeechBot.getText().toString();
                //Valid Question - Try to post
                if ((inputTextTop != null && ! inputTextTop.isEmpty()) && (inputTextBot != null && ! inputTextBot.isEmpty())){
                    //TODO: Make function in question to build with choices, rather than creating by hand
                    //Todo: Network postQuestion should return with error codes
                    String questionBody = inputTextTop + " or " + inputTextBot;
                    Question question = new Question(questionBody, Self.getUser() );
                    Choice c1 = new Choice();
                    Choice c2 = new Choice();
                    c1.setQuestionID(question.getId());
                    c2.setQuestionID(question.getId());
                    c1.setAnswerBody(inputTextTop);
                    c2.setAnswerBody(inputTextBot);
                    question.addChoice(c1);
                    question.addChoice(c2);
                    //Network.postQuestion(getActivity(), question);
                    Network.postQuestionWithImage(getActivity().getApplicationContext(), question, uploadProgress);
                    Toast.makeText(getActivity().getApplicationContext(), "Question Submitted (Todo: switch fragment)",
                            Toast.LENGTH_LONG).show();
                }
                //Invalid Question - Reject
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Your question is not formatted correctly",
                            Toast.LENGTH_LONG).show();
                }
            }
        });



        return v;
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity().getApplicationContext(),
                            getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void disableClicks() {
        if(holder_layout != null)
        {
            disable(holder_layout);
        }
    }

    public static void disable(ViewGroup layout) {
        layout.setEnabled(false);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                disable((ViewGroup) child);
            } else {
                child.setEnabled(false);
            }
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String choices[] = SpeechToText.stringToTwoChoices(result.get(0));

                    textInputSpeechTop.setText(choices[0]);
                    textInputSpeechBot.setText(choices[1]);

                }
                break;
            }

        }
    }


}
