package com.hackathon.light.italk;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hackathon.light.italk.speechrecgontion.OnSpeechRecognitionListener;
import com.hackathon.light.italk.speechrecgontion.OnSpeechRecognitionPermissionListener;
import com.hackathon.light.italk.speechrecgontion.SpeechRecognition;

import java.util.ArrayList;
import java.util.Locale;

import min3d.Shared;
import min3d.animation.AnimationObject3d;
import min3d.core.Renderer;
import min3d.core.Scene;
import min3d.interfaces.ISceneController;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;

public class TabsActivity extends AppCompatActivity implements ISceneController {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    static int record = android.R.drawable.ic_btn_speak_now;
    static int send = android.R.drawable.ic_menu_send;

    //static EditText editText ;
    static ImageButton button;

    static SpeechRecognition speechRecognition;


    public Scene scene;
    protected static GLSurfaceView _glSurfaceView;

    protected Handler _initSceneHander;
    protected Handler _updateSceneHander;

    private boolean _renderContinuously;

    final Runnable _initSceneRunnable = new Runnable()
    {
        public void run() {
            onInitScene();
        }
    };

    final Runnable _updateSceneRunnable = new Runnable()
    {
        public void run() {
            onUpdateScene();
        }
    };

    static AnimationObject3d object3d;

    static Renderer r;

    public static String text;
    public static TextView mSpokenWordTextView;
    private static ImageView deleteSpokenWordIageView;
    private static ImageView saySpokenWordIageView;

    public static Context c;
    private static TextToSpeech sp;
    private Locale d;

    //a list of arrays that stores the signs and the meaning of it and the fran=mes that it contains


    private static String[] meanings = {"hello","bye bye","good","morning"};
    private static int[] beginnings = {1,41,86,124};
    private static int[] endings = {40,85,123,155};


    protected static void glSurfaceViewConfig()
    {
        // Example which makes glSurfaceView transparent (along with setting scene.backgroundColor to 0x0)
         _glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
         _glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        _glSurfaceView.setZOrderOnTop(true);
        // Example of enabling logging of GL operations
        // _glSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        //Text to speech engine

        c = getApplicationContext();
        d = Locale.ENGLISH;
        sp = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    sp.setLanguage(d);
                }
            }
        });

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(1).setText("Interpret");
        tabLayout.getTabAt(0).setText("Simulator");

        tabLayout.setSelectedTabIndicatorColor(Color.BLACK);


        //min3d lib methods that uses open gl animation library
        _initSceneHander = new Handler();
        _updateSceneHander = new Handler();


        //Speech recognition library that uses google voice recognition engine
        try {
            speechRecognition = new SpeechRecognition(this);
            speechRecognition.setSpeechRecognitionPermissionListener(g);
            speechRecognition.setSpeechRecognitionListener(h);
        }catch (Exception e){
            Toast.makeText(TabsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //min3d render setting

        Shared.context(this);
        scene = new Scene(this);
        r = new Renderer(scene);
        Shared.renderer(r);


    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int num = getArguments().getInt(ARG_SECTION_NUMBER);
            if (num == 1) {
                View rootView = inflater.inflate(R.layout.speech_to_text, container, false);
                //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                //editText = (EditText) rootView.findViewById(R.id.editText);
                button = (ImageButton) rootView.findViewById(R.id.imageButton);

                //editText.clearFocus();
                ListView listView = (ListView)rootView.findViewById(R.id.recommondation);
                ArrayList<String> list = new ArrayList<>();
                list.add("hello");
                list.add("bye bye");
                list.add("good");
                list.add("morning");
                ArrayAdapter<String> a = new ArrayAdapter<>(c,R.layout.text,R.id.teext,list);

                listView.setAdapter(a);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        object3d.play(beginnings[position],endings[position]);
                    }
                });



                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //if (editText.getText().toString().equals("a")) {
                            //object3d.play(0,8);
                        //}
                        /*object3d.clear();
                        IParser myParser = Parser.createParser(Parser.Type.MD2, getResources(), "com.hackathon.light.italk:raw/animation2", false);
                        myParser.parse();
                        object3d = myParser.getParsedAnimationObject();
                        object3d.play();*/
                        speechRecognition.startSpeechRecognition();
                    }
                });

                /*editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (editText.getText().toString().equals("")) {
                            button.setImageResource(record);
                        } else {
                            button.setImageResource(send);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });*/


                //GlSurfaceView setting renderer

                _glSurfaceView = (GLSurfaceView) rootView.findViewById(R.id.glSurfaceView);
                glSurfaceViewConfig();
                _glSurfaceView.setRenderer(r);
                _glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

                return rootView;
            }else {
                View rootView = inflater.inflate(R.layout.sign_gridview, container, false);
                RecyclerView r = (RecyclerView)rootView.findViewById(R.id.recycler_card);
                r.setHasFixedSize(true);

                //setting grid layout manager class to recyclerview and using the adapter
                //GridLayoutManager manager = new GridLayoutManager(getApplicationContext(),2);
                GridAutofitLayoutManager manager = new GridAutofitLayoutManager(c, 300);
                manager.setOrientation(LinearLayoutManager.VERTICAL);
                r.setLayoutManager(manager);
                r.setAdapter(new SignAdapter(c));
                mSpokenWordTextView = (TextView)rootView.findViewById(R.id.spoken_word_text);
                mSpokenWordTextView.setText("Click a sign letter to comulate a word");
                text = "";
                ImageView buttonDelete = (ImageView)rootView.findViewById(R.id.delete_spoken_word);
                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!text.isEmpty()){
                            text = text.substring(0,text.length()-1);
                            mSpokenWordTextView.setText(text);
                        }
                    }
                });
                ImageView buttonSpeak = (ImageView)rootView.findViewById(R.id.say_spoken_word);
                buttonSpeak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sp.speak(text,TextToSpeech.QUEUE_FLUSH,null);
                        text = "";
                        mSpokenWordTextView.setText(text);
                    }
                });

                return rootView;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }
    }


    //speech recognition permission listner

    OnSpeechRecognitionPermissionListener g = new OnSpeechRecognitionPermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(TabsActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied() {
            Toast.makeText(TabsActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
        }
    };


    //speech recognition listner

    OnSpeechRecognitionListener h = new OnSpeechRecognitionListener() {
        @Override
        public void OnSpeechRecognitionStarted() {
            Toast.makeText(TabsActivity.this,"Listening started",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void OnSpeechRecognitionStopped() {
            Toast.makeText(TabsActivity.this,"Listening stopped",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void OnSpeechRecognitionFinalResult(String finalSentence) {

            switch (finalSentence.toLowerCase()){
                case "hello":
                    object3d.play(0,39);
                    break;
                case "bye bye":
                    object3d.play(40,84);
                    break;
                case "good":
                    object3d.play(85,122);
                    break;
                case "morning":
                    object3d.play(123,154);
                    break;
                case "good morning":
                    object3d.play(85,154);
                    break;
                default:
                    Toast.makeText(c,"this word is not supported yet",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void OnSpeechRecognitionCurrentResult(String currentWord) {
            //b.setText(currentWord);
        }

        @Override
        public void OnSpeechRecognitionError(int errorCode, String errorMsg) {
            Toast.makeText(TabsActivity.this,errorMsg,Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onDestroy() {
        //if (speechRecognition != null) {speechRecognition.stopSpeechRecognition();}
        super.onDestroy();

    }



    //min3d lib methods

    protected GLSurfaceView glSurfaceView()
    {
        return _glSurfaceView;
    }



    public void renderContinuously(boolean $b)
    {
        _renderContinuously = $b;
        if (_renderContinuously)
            _glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        else
            _glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public Handler getInitSceneHandler()
    {
        return _initSceneHander;
    }

    public Handler getUpdateSceneHandler()
    {
        return _updateSceneHander;
    }

    public Runnable getInitSceneRunnable()
    {
        return _initSceneRunnable;
    }

    public Runnable getUpdateSceneRunnable()
    {
        return _updateSceneRunnable;
    }

    public void onUpdateScene()
    {
    }

    public void initScene()
    {
        scene.backgroundColor().setAll(0, 0, 0, 0);
        scene.lights().add(new Light());
        scene.lights().add(new Light());
        Light myLight = new Light();
        myLight.position.setZ(150);
        scene.lights().add(myLight);

        IParser myParser = Parser.createParser(Parser.Type.MD2, getResources(), "com.hackathon.light.italk:raw/animation", false);
        myParser.parse();
        object3d = myParser.getParsedAnimationObject();


        //cubeObject3D = myParser.getParsedObject();
        object3d.position().x = 0;
        object3d.position().y = -5;
        object3d.position().z = -20;
        // Depending on the model you will need to change the scale
        object3d.scale().x = object3d.scale().y = object3d.scale().z = 0.04f;//0.0065
        object3d.rotation().y = 0;//0
        object3d.rotation().x = 180;//180
        object3d.rotation().z = 180;//180
        scene.addChild(object3d);
        object3d.setFps(70);
    }

    public void updateScene()
    {
    }

    public void onInitScene()
    {
    }

    public static void updateText(){

    }



}
