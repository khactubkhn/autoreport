package com.gpch.login.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class MergeFileExcelsUtil {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    @Autowired
    ReadFileExcelUtil readFileExcelUtil;
    public List<Vector<String>> merge(int roomId){
        List<Vector<String>> result = new ArrayList<>();
        List<Vector<String>> listWho = readFileExcelUtil.readWhoFile(roomId);
        List<Vector<String>> listWhat = readFileExcelUtil.readWhatFile(roomId);
        if(listWhat.size() == 0 || listWho.size() == 0){
            return new ArrayList<>();
        }
        System.out.println("WHO: " + listWho.toString());
        System.out.println("WHAT: " + listWhat.toString());

        Map<String, List<String>> listTanSo = new HashMap<>();
        for(Vector<String> what: listWhat){
            if(listTanSo.containsKey(what.get(3))){
                List<String> oldSpeakers = listTanSo.get(what.get(3));
                List<String> newSpeakers = getSpeakerByF(what, listWho);
                List<String> unionSpeaker = union(oldSpeakers, newSpeakers);
                listTanSo.remove(what.get(3));
                listTanSo.put(what.get(3), unionSpeaker);
            }else{
                listTanSo.put(what.get(3), getSpeakerByF(what, listWho));
            }
        }

        for(Vector<String> what: listWhat){
            what.add(4, listTanSo.get(what.get(3)).get(0));
        }
        System.out.println(listWhat.toString());
        return listWhat;
    }

    public List<String> getSpeakerByF(Vector<String> what, List<Vector<String>> listWho){
        List<String> listSpeaker = new ArrayList<>();
        long start_1 = parseDate(what.get(0));
        long end_1 = parseDate(what.get(1));
        for(Vector<String> who: listWho){
            long start_2 = parseDate(who.get(0));
            long end_2 = parseDate(who.get(1));
            if(checkTime(start_1, end_1, start_2, end_2)){
                listSpeaker.add(who.get(2));
            }
        }
        return listSpeaker;
    }


    public long parseDate(String date){
        Date d = null;
        try {
            d = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
        return d.getTime();
    }

    public boolean checkTime(long start_1, long end_1, long start_2, long end_2){
        if(start_1 == start_2 && end_1 == end_2){
            return true;
        }
        return false;
    }

    public List<String> union(List<String> l1, List<String> l2){
        List<String> result = new ArrayList<>();
        for(String e1: l1){
            for(String e2: l2){
                if(e1.endsWith(e2)){
                    result.add(e1);
                    break;
                }
            }
        }
        return result;
    }

}
