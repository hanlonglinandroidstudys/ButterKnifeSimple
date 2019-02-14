package hanlonglin.com.butterknifesimple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import hanlonglin.com.butterknife_annotation.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.txt_title)
    TextView txt_title;

    @BindView(R.id.txt_content)
    TextView txt_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        txt_title.setText("hahah");
        txt_content.setText("hehe");
    }
}
