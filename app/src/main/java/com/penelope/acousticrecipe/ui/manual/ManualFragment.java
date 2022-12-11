package com.penelope.acousticrecipe.ui.manual;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.penelope.acousticrecipe.R;
import com.penelope.acousticrecipe.databinding.DialogTimerBinding;
import com.penelope.acousticrecipe.databinding.FragmentManualBinding;
import com.penelope.acousticrecipe.services.TimerService;
import com.penelope.acousticrecipe.utils.Consts;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ManualFragment extends Fragment implements TextToSpeech.OnInitListener, ServiceConnection {

    private FragmentManualBinding binding;
    private ManualViewModel viewModel;

    private TextToSpeech tts;
    private MediaPlayer mediaPlayer;

    private ActivityResultLauncher<Intent> voiceRecognitionLauncher;
    private TimerService timerService;


    public ManualFragment() {
        super(R.layout.fragment_manual);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        voiceRecognitionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == Activity.RESULT_OK && data != null) {
                        ArrayList<String> words = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        viewModel.onVoiceRecognized(words);
                    }
                }
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentManualBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(ManualViewModel.class);
        tts = new TextToSpeech(requireContext(), this);

        binding.imageViewPrev.setOnClickListener(v -> viewModel.onPrevClick());
        binding.imageViewNext.setOnClickListener(v -> viewModel.onNextClick());
        binding.imageViewPlayStop.setOnClickListener(v -> viewModel.onPlayStopClick());
        binding.fabVoice.setOnClickListener(v -> viewModel.onVoiceClick());
        binding.fabTimer.setOnClickListener(v -> viewModel.onTimerClick());

        BooleansAdapter adapter = new BooleansAdapter();
        binding.recyclerSign.setAdapter(adapter);
        binding.recyclerSign.setHasFixedSize(true);

        adapter.setOnItemSelectedListener(position -> viewModel.onSignClick(position));

        viewModel.getManual().observe(getViewLifecycleOwner(), manual ->
                binding.textViewManualDescription.setText(manual));

        viewModel.getManualImage().observe(getViewLifecycleOwner(), manualImage -> {
            String imageUrl = (manualImage != null && !manualImage.isEmpty()) ? manualImage : Consts.URL_IMAGE_DEFAULT;
            Glide.with(this)
                    .load(imageUrl)
                    .into(binding.imageViewManual);
        });

        viewModel.getIndex().observe(getViewLifecycleOwner(), index -> {
            List<Boolean> signs = new ArrayList<>();
            for (int i = 0; i < viewModel.getNumOfSteps(); i++) {
                signs.add(i == index);
            }
            adapter.submitList(signs);

            String strStep = String.format(Locale.getDefault(), "Step %d", index + 1);
            binding.textViewManualStep.setText(strStep);

            binding.imageViewPrev.setAlpha(index > 0 ? 1.0f : 0.3f);
            binding.imageViewNext.setAlpha(index < viewModel.getNumOfSteps() - 1 ? 1.0f : 0.3f);
        });

        viewModel.isPlaying().observe(getViewLifecycleOwner(), isPlaying ->
                binding.imageViewPlayStop.setImageResource(isPlaying ?
                        R.drawable.ic_stop : R.drawable.ic_play));

        viewModel.isReady().observe(getViewLifecycleOwner(), isReady -> {
            binding.imageViewPlayStop.setAlpha(isReady ? 1.0f : 0.3f);
            binding.progressBar4.setVisibility(isReady ? View.INVISIBLE : View.VISIBLE);
        });

        viewModel.getScript().observe(getViewLifecycleOwner(), script -> {
            if (script != null) {
                speak(script);
            } else {
                tts.stop();
            }
        });

        viewModel.getMinutes().observe(getViewLifecycleOwner(), minutes ->
                binding.fabTimer.setVisibility(minutes != null ? View.VISIBLE : View.GONE));

        secondsTimer.observe(getViewLifecycleOwner(), seconds -> {
            if (seconds != null) {
                String currentManual = viewModel.getManual().getValue();
                if (timerService != null && timerService.getManual().equals(currentManual)) {
                    binding.textViewTimer.setVisibility(View.VISIBLE);
                    String strTime = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
                    binding.textViewTimer.setText(strTime);
                } else {
                    binding.textViewTimer.setVisibility(View.INVISIBLE);
                }
            } else {
                binding.textViewTimer.setVisibility(View.INVISIBLE);
            }
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof ManualViewModel.Event.InformNextManual) {
                playSound();
            } else if (event instanceof ManualViewModel.Event.ShowGeneralMessage) {
                String message = ((ManualViewModel.Event.ShowGeneralMessage) event).message;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            } else if (event instanceof ManualViewModel.Event.PromptTimerDuration) {
                int minutes = ((ManualViewModel.Event.PromptTimerDuration) event).minutes;
                showTimerDialog(minutes);
            } else if (event instanceof ManualViewModel.Event.StartTimer) {
                int minutes = ((ManualViewModel.Event.StartTimer) event).minutes;
                String foodName = ((ManualViewModel.Event.StartTimer) event).foodName;
                String manual = ((ManualViewModel.Event.StartTimer) event).manual;
                startTimerService(minutes, foodName, manual);
                Toast.makeText(requireContext(), "타이머가 시작되었습니다", Toast.LENGTH_SHORT).show();
            } else if (event instanceof ManualViewModel.Event.PromptVoice) {
                showVoiceRecognitionDialog();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(requireContext(), TimerService.class);
        requireContext().bindService(intent, this, 0);
    }

    @Override
    public void onPause() {
        requireContext().unbindService(this);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        tts.stop();
        tts.shutdown();
        tts = null;
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.KOREA);

            if (result != TextToSpeech.LANG_MISSING_DATA) {

                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String s) {
                    }

                    @Override
                    public void onDone(String s) {
                        binding.getRoot().postDelayed(() -> viewModel.onSpeechDone(), 1000);
                    }

                    @Override
                    public void onError(String s) {
                    }
                });

                viewModel.onSpeechReady();
                return;
            }
        }

        viewModel.onSpeechNotAvailable();
    }

    private void speak(String s) {

        Bundle bundle = new Bundle();
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
        tts.speak(s, TextToSpeech.QUEUE_FLUSH, bundle, "id");
    }

    private void playSound() {

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.next);
            mediaPlayer.setOnCompletionListener(mp -> stopSound());
        }

        mediaPlayer.start();
    }

    private void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void showTimerDialog(int minutes) {

        DialogTimerBinding binding = DialogTimerBinding.inflate(getLayoutInflater());
        binding.numberPicker.setMinValue(1);
        binding.numberPicker.setMaxValue(60);
        binding.numberPicker.setValue(minutes);

        new AlertDialog.Builder(requireContext())
                .setTitle("타이머 설정")
                .setView(binding.getRoot())
                .setPositiveButton("확인", (dialog, which) ->
                        viewModel.onTimerSubmit(binding.numberPicker.getValue()))
                .setNegativeButton("취소", null)
                .show();
    }

    private void startTimerService(int minutes, String foodName, String manual) {

        Intent intent = new Intent(requireContext(), TimerService.class);
        intent.putExtra("minutes", minutes);
        intent.putExtra("food_name", foodName);
        intent.putExtra("manual", manual);
        requireActivity().stopService(intent);
        requireActivity().startForegroundService(intent);
        requireActivity().bindService(intent, this, 0);
    }


    private void showVoiceRecognitionDialog() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "재료 이름을 말씀해주세요");

        voiceRecognitionLauncher.launch(intent);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        timerService = ((TimerService.TimerServiceBinder) service).getService();
        timerObserverThread = new TimerObserverThread();
        timerObserverThread.start();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        timerService = null;
        try {
            timerObserverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timerObserverThread = null;
    }

    private class TimerObserverThread extends Thread {
        @Override
        public void run() {
            while (timerService != null && TimerService.isStarted) {
                secondsTimer.postValue(timerService.getSeconds());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            secondsTimer.postValue(null);
        }
    }

    private TimerObserverThread timerObserverThread;
    private final MutableLiveData<Integer> secondsTimer = new MutableLiveData<>();

}









