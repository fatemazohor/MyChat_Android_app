package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.mychat.model.UserModel;
import com.example.mychat.utill.AndroidUtil;
import com.example.mychat.utill.FirebaseUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.zoom_in);
        ImageView imageView = findViewById(R.id.logoImage);
        imageView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // FCM
                if(getIntent().getExtras() != null){
                    String userId = getIntent().getExtras().getString("userId");
                    FirebaseUtil.allUserCollectionReference().document(userId).get()
                            .addOnCompleteListener(task -> {
                               if(task.isSuccessful()){
                                   UserModel model = task.getResult().toObject(UserModel.class);

                                   Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                                   mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                   startActivity(mainIntent);

                                   Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                   AndroidUtil.passUserModelAsIntent(intent,model);
                                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                   startActivity(intent);
                                   finish();
                               }
                            });

                }else {

                    // login check
                    if (FirebaseUtil.isLoggedIn()) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginPhoneNumber.class);
                        startActivity(intent);
                    }
                    finish();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}