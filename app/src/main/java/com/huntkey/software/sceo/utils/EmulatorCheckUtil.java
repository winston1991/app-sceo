package com.huntkey.software.sceo.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by chenl on 2017/9/6.
 */

public class EmulatorCheckUtil {

    private static String[]known_qemu_drivers = {"goldfish" };
    private static String[]known_numbers = {"15555215554","15555215556",
            "15555215558","15555215560","15555215562","15555215564",
            "15555215566","15555215568","15555215570","15555215572",
            "15555215574","15555215576","15555215578","15555215580",
            "15555215582","15555215584",};
    private static String[]known_device_ids = {"000000000000000" // 默认ID
    };
    private static String[]known_imsi_ids = {"310260000000000" // 默认的 imsi id
    };
    private static String[] known_bluestacks = {"/data/app/com.bluestacks.appmart-1.apk", "/data/app/com.bluestacks.BstCommandProcessor-1.apk",
            "/data/app/com.bluestacks.help-1.apk", "/data/app/com.bluestacks.home-1.apk", "/data/app/com.bluestacks.s2p-1.apk",
            "/data/app/com.bluestacks.searchapp-1.apk", "/data/bluestacks.prop", "/data/data/com.androVM.vmconfig",
            "/data/data/com.bluestacks.accelerometerui", "/data/data/com.bluestacks.appfinder", "/data/data/com.bluestacks.appmart",
            "/data/data/com.bluestacks.appsettings", "/data/data/com.bluestacks.BstCommandProcessor", "/data/data/com.bluestacks.bstfolder",
            "/data/data/com.bluestacks.help", "/data/data/com.bluestacks.home", "/data/data/com.bluestacks.s2p", "/data/data/com.bluestacks.searchapp",
            "/data/data/com.bluestacks.settings", "/data/data/com.bluestacks.setup", "/data/data/com.bluestacks.spotlight", "/mnt/prebundledapps/bluestacks.prop.orig"
    };

    public static Boolean CheckQEmuDriverFile(){
        File driver_file =new File("/proc/tty/drivers");
        if(driver_file.exists()&& driver_file.canRead()){
            byte[]data =new byte[(int)driver_file.length()];
            try{
                InputStream inStream =new FileInputStream(driver_file);
                inStream.read(data);
                inStream.close();
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            String driver_data =new String(data);
            for(String known_qemu_driver : known_qemu_drivers){
                if(driver_data.indexOf(known_qemu_driver)!= -1){
                    return true;
                }
            }
        }
        return false;
    }

    public static Boolean CheckPhoneNumber(Context context){
        TelephonyManager telephonyManager =(TelephonyManager)context
                .getSystemService(Context.TELEPHONY_SERVICE);

        String phonenumber =telephonyManager.getLine1Number();

        for(String number :known_numbers){
            if(number.equalsIgnoreCase(phonenumber)){
                return true;
            }
        }
        return false;
    }

    public static Boolean CheckDeviceIDS(Context context){
        TelephonyManager telephonyManager = (TelephonyManager)context
                .getSystemService(Context.TELEPHONY_SERVICE);

        String device_ids =telephonyManager.getDeviceId();

        for(String know_deviceid : known_device_ids){
            if(know_deviceid.equalsIgnoreCase(device_ids)){
                return true;
            }
        }
        return false;
    }

    public static Boolean CheckImsiIDS(Context context){
        TelephonyManager telephonyManager =(TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);

        String imsi_ids =telephonyManager.getSubscriberId();

        for(String know_imsi :known_imsi_ids){
            if(know_imsi.equalsIgnoreCase(imsi_ids)){
                return true;
            }
        }
        return false;
    }

    public static Boolean CheckEmulatorBuild(Context context){
        String BRAND =android.os.Build.BRAND;
        String DEVICE =android.os.Build.DEVICE;
        String HARDWARE =android.os.Build.HARDWARE;
        String MODEL =android.os.Build.MODEL;
        String PRODUCT =android.os.Build.PRODUCT;
        if(BRAND =="generic" ||DEVICE =="generic"
                ||MODEL =="sdk" ||PRODUCT =="sdk"
                ||HARDWARE =="goldfish")
        {
            return true;
        }
        return false;
    }

    public static boolean checkBlueStacksFiles() {
        for (int i = 0; i < known_bluestacks.length; i++) {
            String file_name = known_bluestacks[i];
            File qemu_file = new File(file_name);
            if (qemu_file.exists()) {
                return true;
            }
        }
        return false;
    }

    public static boolean readCpuInfo() {
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            StringBuffer sb = new StringBuffer();
            String readLine = "";
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine);
            }
            responseReader.close();
            result = sb.toString().toLowerCase();
        } catch (IOException ex) {
        }
        if (result.contains("intel") || result.contains("amd") || result.contains("google_sdk")
                || result.contains("sdk") || result.contains("sdk_x86") || result.contains("vbox86p")){
            return true;
        }
        return false;
    }

}
