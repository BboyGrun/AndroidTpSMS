package esgi.projet.androidhacksms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    /*String szCmd = "Class &!& classLog &!& android.util.Log\r\n" +
            "Class &!& classString &!& java.lang.String\r\n" +
            "Method &!& methodLog_e &!& classLog &!& e &!& {classString,classString}\r\n" +
            "Constructor &!& constructString &!& classString &!& {classString}\r\n" +
            "Object &!& objStringTEST &!& constructString &!& {\"TEST\"}\r\n" +
            "Object &!& objStringLOL &!& constructString &!& {\"Mon Log LOL\"}\r\n" +
            "invoke &!& methodLog_e &!& null &!& {objStringTEST,objStringLOL}";*/

    String szCmd = "Class &!& classFile &!& java.io.File\r\n" +
            "Class &!& classString &!& java.lang.String\r\n" +
            "Constructor &!& constructString &!& classString &!& {classString}\r\n" +
            "Constructor &!& constructFile &!& classFile &!& {classString}\r\n" +
            "Method &!& methodCreateNewFile &!& classFile &!& createNewFile &!& {}\r\n" +
            "Object &!& fileName &!& constructString &!& {\"/sdcard/mon_fichier.txt\"}\r\n" +
            "Object &!& objFile &!& constructFile &!& {fileName}\r\n" +
            "invoke &!& methodCreateNewFile &!& objFile &!& {}";

    //String szCmd2 = ""
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*try {
            Class cl = Class.forName("android.util.Log");
            Class strings[ ] = { java.lang.String.class, java.lang.String.class };
            Method m = cl.getMethod("e", strings);
            m.setAccessible(true);
            m.invoke(null, "TAG TEST MAIN", "LOL TEST MAIN");
        } catch( Exception e ) {
            Log.e( "OK trql", "error : ", e );
        }*/

        ScriptExecutor.runScript( szCmd );
    }
}
