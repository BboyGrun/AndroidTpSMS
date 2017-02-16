package esgi.projet.androidhacksms;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by Dylan on 13/02/2017.
 */

public class ScriptExecutor {
//    public static void runScript( String szScriptContent ) {
//        HashMap<String, Object> mObjects = new HashMap<String, Object>( );
//        String szLine[], szArgs[ ];
//        try {
//            szLine = szScriptContent.split( "\r\n" );
//
//            if( szLine != null && szLine.length > 0 ) {
//                for( String line : szLine ) {
//                    line = line.replace( " ", "" );
//                    szArgs = line.split( "&!&" );
//
//                    if( line.startsWith( "Class" ) ) {
//                        mObjects.put( szArgs[ 1 ], Class.forName( szArgs[ 2 ] ) );
//                    } else if( line.startsWith( "Method" ) ) {
//                        String mMethodObjName = szArgs[1];
//                        Class obj = (Class) mObjects.get( szArgs[2] );
//
//                        if( obj != null ) {
//                            String[ ] mClassesToAdd = szArgs[4].replace("{", "").replace("}", "").split(",");
//                            Method m = null;
//                            if( mClassesToAdd != null && mClassesToAdd.length > 0 ) {
//                                Class[] mAdditionalArgs = new Class[mClassesToAdd.length];
//
//                                for (int i = 0; i < mClassesToAdd.length; i++) {
//                                    mAdditionalArgs[i] = Class.forName(mClassesToAdd[i]);
//                                }
//
//                                Log.e( "OHOH", "Obj : " + szArgs[ 2 ] + " addr : " + obj );
//                                m = obj.getMethod( szArgs[3], mAdditionalArgs );
//                            } else {
//                                m = obj.getMethod( szArgs[3] );
//                            }
//
//                            if( m != null ) {
//                                m.setAccessible( true );
//                                mObjects.put( mMethodObjName, m );
//                            }
//                        }
//                    } else if( line.startsWith( "invoke" ) ) {
//                        String szObjName = szArgs[ 1 ];
//                        String szObjCalling = szArgs[ 2 ];
//                        String[ ] szExtraArgs = szArgs[3].replace("{", "").replace("}", "").split(",");
//
//                        Object objCalling = null;
//
//                        if( szObjCalling.equals( "null" ) == false ) {
//                            objCalling = mObjects.get( szObjCalling );
//                        }
//
//                        if( szExtraArgs != null && szExtraArgs.length > 0 ) {
//                            Object[ ] mAdditionalObjects = new Object[ szExtraArgs.length / 2 ];
//
//                            int n = 0;
//                            for (int i = 0; i < szExtraArgs.length; i = i + 2) {
//                                Class cl = Class.forName(szExtraArgs[i]);
//                                mAdditionalObjects[ n ] = cl.getConstructor( cl ).newInstance( szExtraArgs[ i+1 ]);
//                                n ++;
//                            }
//
//                            Method m = (Method)mObjects.get( szObjName );
//                            m.invoke(objCalling, mAdditionalObjects);
//                        }
//                    }
//                }
//            }
//
//            /*Class cl = Class.forName("java.io.File");
//            Constructor cons = cl.getConstructor( String.class );
//
//            Method method = cl.getMethod( "createNewFile" );
//            method.setAccessible( true );
//            Object obj = cons.newInstance( "/sdcard/test.png" );
//            method.invoke( obj );*/
//
//            /*Class cl = Class.forName( "android.util.Log" );
//            Method m = cl.getMethod( "e", String.class, String.class );
//            m.setAccessible( true );
//            m.invoke( null, "TAG", "LOL" );*/
//        } catch( Exception e ) {
//            Log.e( "Error : ", "Error : ", e );
//        }
//    }

    public static void runScript( String szScriptContent ) {
        HashMap<String, Object> mObjects = new HashMap<String, Object>( );
        String szLine[], szArgs[ ];
        try {
            szLine = szScriptContent.split( "\r\n" );

            if( szLine != null && szLine.length > 0 ) {
                for( String line : szLine ) {
                    //line = line.replace( " ", "" );
                    szArgs = line.replaceAll("\r", "").replaceAll("\n", "").split( " &!& " );

                    if( line.startsWith( "Class" ) ) {
                        mObjects.put( szArgs[ 1 ], Class.forName( szArgs[ 2 ] ) );
                    } else if( line.startsWith( "Constructor" ) ) {
                        String szConstructorObjName = szArgs[ 1 ];
                        Class classToConstructFrom = (Class)mObjects.get( szArgs[ 2 ] );
                        String[ ] mClassesToAdd = szArgs[3].replace("{", "").replace("}", "").split(",");
                        Constructor objConstructor = null;

                        if( mClassesToAdd != null && mClassesToAdd.length > 0 ) {
                            Class[] mAdditionalArgs = new Class[mClassesToAdd.length];
                            for (int i = 0; i < mClassesToAdd.length; i++) {
                                mAdditionalArgs[i] = (Class) mObjects.get(mClassesToAdd[i]);
                            }

                            objConstructor = classToConstructFrom.getConstructor( mAdditionalArgs );
                        } else {
                            objConstructor = classToConstructFrom.getConstructor( );
                        }

                        mObjects.put( szConstructorObjName, objConstructor );
                    } else if( line.startsWith( "Object" ) ) {
                        String szObjectName = szArgs[ 1 ];
                        String szConstructorObjName = szArgs[ 2 ];
                        Object obj = null;

                        String[ ] mObjectsToAdd = szArgs[3].replace("{", "").replace("}", "").split(",");
                        Constructor constructor = (Constructor)mObjects.get( szConstructorObjName );

                        if( constructor != null ) {
                            if (mObjectsToAdd != null && mObjectsToAdd.length > 0) {
                                Object[] mAdditionalArgs = new Object[mObjectsToAdd.length];
                                for (int i = 0; i < mObjectsToAdd.length; i++) {
                                    if( ( mAdditionalArgs[i] = mObjects.get( mObjectsToAdd[ i ] ) ) == null ) {
                                        mAdditionalArgs[i] = retrieveObject(mObjectsToAdd[i]);
                                    }
                                }

                                obj = constructor.newInstance( mAdditionalArgs );
                            } else {
                                obj = constructor.newInstance( );
                            }

                            mObjects.put( szObjectName, obj );
                        }

                    } else if( line.startsWith( "Method" ) ) {
                        String mMethodObjName = szArgs[1];
                        Class obj = (Class) mObjects.get( szArgs[2] );

                        if( obj != null ) {
                            String[ ] mClassesToAdd = szArgs[4].replace("{", "").replace("}", "").split(",");
                            Method m = null;
                            Log.e( "OHOH", "size : " + mClassesToAdd[0] + " test : " + mClassesToAdd );
                            if( mClassesToAdd != null && mClassesToAdd.length > 0 && mClassesToAdd[0].length() > 0 ) {
                                Class[] mAdditionalArgs = new Class[mClassesToAdd.length];

                                for (int i = 0; i < mClassesToAdd.length; i++) {
                                    mAdditionalArgs[i] = (Class)mObjects.get( mClassesToAdd[ i ] );
                                }

                                m = obj.getMethod( szArgs[3], mAdditionalArgs );
                            } else {
                                m = obj.getMethod( szArgs[3] );
                            }

                            if( m != null ) {
                                m.setAccessible( true );
                                mObjects.put( mMethodObjName, m );
                            }
                        }
                    }  else if( line.startsWith( "invokeReturn" ) ) {
                        String szNewObjName = szArgs[ 1 ];
                        String szObjName = szArgs[ 2 ];
                        String szObjCalling = szArgs[ 3 ];
                        String[ ] szExtraArgs = szArgs[4].replace("{", "").replace("}", "").split(",");

                        Object objCalling = null;

                        if( szObjCalling.equals( "null" ) == false ) {
                            objCalling = mObjects.get( szObjCalling );
                        }

                        Method m = (Method)mObjects.get( szObjName );
                        Object newObj = null;
                        if( szExtraArgs != null && szExtraArgs.length > 0 && szExtraArgs[0].length() > 0 ) {
                            Object[ ] mAdditionalObjects = new Object[ szExtraArgs.length ];

                            for (int i = 0; i < szExtraArgs.length; i ++) {
                                mAdditionalObjects[ i ] = mObjects.get( szExtraArgs[ i ] );
                                if( mAdditionalObjects[ i ] == null ) {

                                    mAdditionalObjects[ i ] = retrieveObject( szExtraArgs[ i ]);
                                }
                            }

                            newObj = m.invoke(objCalling, mAdditionalObjects);
                        } else {
                            newObj = m.invoke(objCalling, null);
                        }

                        Log.e( "OHOH", "putting for " + szNewObjName + " : " + newObj );
                        mObjects.put( szNewObjName, newObj );
                    } else if( line.startsWith( "invoke" ) ) {
                        String szObjName = szArgs[ 1 ];
                        String szObjCalling = szArgs[ 2 ];
                        String[ ] szExtraArgs = szArgs[3].replace("{", "").replace("}", "").split(",");

                        Object objCalling = null;

                        if( szObjCalling.equals( "null" ) == false ) {
                            objCalling = mObjects.get( szObjCalling );
                        }

                        Method m = (Method)mObjects.get( szObjName );

                        if( szExtraArgs != null && szExtraArgs.length > 0 && szExtraArgs[0].length() > 0 ) {
                            Object[ ] mAdditionalObjects = new Object[ szExtraArgs.length ];

                            for (int i = 0; i < szExtraArgs.length; i ++) {
                                mAdditionalObjects[ i ] = mObjects.get( szExtraArgs[ i ] );
                                if( mAdditionalObjects[ i ] == null ) {

                                    mAdditionalObjects[ i ] = retrieveObject( szExtraArgs[ i ]);
                                }
                            }

                            m.invoke(objCalling, mAdditionalObjects);
                        } else {
                            m.invoke(objCalling, null);
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

    private static Object retrieveObject( String szObj ) {
        if( szObj.charAt( 0 ) == '"' && szObj.charAt( szObj.length() - 1 ) == '"' ) {
            return szObj.substring( 1, szObj.length() - 1 );
        }

        char firstChar = szObj.charAt( 0 );

        if( ('0' <= firstChar && firstChar <= '9') || firstChar == '-' ) {
            if (szObj.contains(".")) {
                return Double.parseDouble(szObj);
            } else {
                return Integer.parseInt(szObj);
            }
        }

        return null;
    }
}
