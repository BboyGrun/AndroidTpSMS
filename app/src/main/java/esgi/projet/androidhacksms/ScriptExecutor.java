package esgi.projet.androidhacksms;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by Dylan on 13/02/2017.
 */

public class ScriptExecutor {
    public static void runScript( String szScriptContent ) {
        HashMap<String, Object> mObjects = new HashMap<String, Object>( );
        String szLine[], szArgs[ ];
        try {
            szLine = szScriptContent.split( "\r\n" );

            if( szLine != null && szLine.length > 0 ) {
                for( String line : szLine ) {
                    line = line.replace( " ", "" );
                    szArgs = line.split( "&!&" );

                    if( line.startsWith( "Class" ) ) {
                        mObjects.put( szArgs[ 1 ], Class.forName( szArgs[ 2 ] ) );
                    } else if( line.startsWith( "Method" ) ) {
                        String mMethodObjName = szArgs[1];
                        Class obj = (Class) mObjects.get( szArgs[2] );

                        if( obj != null ) {
                            String[ ] mClassesToAdd = szArgs[4].replace("{", "").replace("}", "").split(",");
                            Method m = null;
                            if( mClassesToAdd != null && mClassesToAdd.length > 0 ) {
                                Class[] mAdditionalArgs = new Class[mClassesToAdd.length];

                                for (int i = 0; i < mClassesToAdd.length; i++) {
                                    mAdditionalArgs[i] = Class.forName(mClassesToAdd[i]);
                                }

                                Log.e( "OHOH", "Obj : " + szArgs[ 2 ] + " addr : " + obj );
                                m = obj.getMethod( szArgs[3], mAdditionalArgs );
                            } else {
                                m = obj.getMethod( szArgs[3] );
                            }

                            if( m != null ) {
                                m.setAccessible( true );
                                mObjects.put( mMethodObjName, m );
                            }
                        }
                    } else if( line.startsWith( "invoke" ) ) {
                        String szObjName = szArgs[ 1 ];
                        String szObjCalling = szArgs[ 2 ];
                        String[ ] szExtraArgs = szArgs[3].replace("{", "").replace("}", "").split(",");

                        Object objCalling = null;

                        if( szObjCalling.equals( "null" ) == false ) {
                            objCalling = mObjects.get( szObjCalling );
                        }

                        if( szExtraArgs != null && szExtraArgs.length > 0 ) {
                            Object[ ] mAdditionalObjects = new Object[ szExtraArgs.length / 2 ];

                            int n = 0;
                            for (int i = 0; i < szExtraArgs.length; i = i + 2) {
                                Class cl = Class.forName(szExtraArgs[i]);
                                mAdditionalObjects[ n ] = cl.getConstructor( cl ).newInstance( szExtraArgs[ i+1 ]);
                                n ++;
                            }

                            Method m = (Method)mObjects.get( szObjName );
                            m.invoke(objCalling, mAdditionalObjects);
                        }
                    }
                }
            }

            /*Class cl = Class.forName("java.io.File");
            Constructor cons = cl.getConstructor( String.class );

            Method method = cl.getMethod( "createNewFile" );
            method.setAccessible( true );
            Object obj = cons.newInstance( "/sdcard/test.png" );
            method.invoke( obj );*/

            /*Class cl = Class.forName( "android.util.Log" );
            Method m = cl.getMethod( "e", String.class, String.class );
            m.setAccessible( true );
            m.invoke( null, "TAG", "LOL" );*/
        } catch( Exception e ) {
            Log.e( "Error : ", "Error : ", e );
        }
    }
}
