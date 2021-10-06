/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.text.SimpleDateFormat;
import java.util.Date;

import dto.Injection;
import dto.Menu;
import dto.Province;
import dto.Vaccine;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Scanner;
import javax.swing.text.StyledEditorKit;
import util.MyToys;

/**
 *
 * @author Walter White
 */
public class InjectionList {

    StudentList sList = new StudentList();
    VaccineList vList = new VaccineList();
    ProvinceList pList = new ProvinceList();
    ArrayList<Injection> injectionList = new ArrayList<>();

    // Search doctor return pos
    public int searchInjectionByID(int injectionID) {
        for (int i = 0; i < injectionList.size(); i++) {
            if (injectionID == injectionList.get(i).getId()) {
                return i;
            }
        }
        return -1;
    }
    // Search injection by student id

    public int searchInjectionByID(String StudentID) {
        for (int i = 0; i < injectionList.size(); i++) {
            if (StudentID.equalsIgnoreCase(injectionList.get(i).getStudentId())) {
                return i;
            }
        }
        return -1;
    }

    // search injection ra nguyên injection
    public Injection searchInjection(int injectionID) {
        if (injectionList.isEmpty()) {
            return null;
        }

        for (Injection injection : injectionList) {
            if (injection.getId() == injectionID) {
                return injection;
            }
        }
        return null;

    }

    // add new injection
    public void AddFirstInjection() throws ParseException {
        Province a;
        String i;
        int pos1, pos, id, vaccineID;
        String injectionPlace1, studentId, injectionPlace2 = null;
        Date injectionDate1, injectionDate2 = null;
        Date today = new Date();
        Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));
        id = generateID();
        do {
            do {
                studentId = MyToys.getID("Enter student's ID (AAXXXXXX): ",
                        "The format of ID is AAXXXXXX", "^((SE)|(SS))\\d{6}$");
                pos = sList.searchStudentByID(studentId);
                pos1 = searchInjectionByID(studentId);

                if (pos != -1 && pos1 == -1) {
                    System.out.println("Valid");

                } else {
                    if (pos1 != -1) {
                        System.out.println("student have been add!");
                    }
                    System.out.println("Student ID is invalid, please re_enter!!");
                    System.out.println("Student list:");
                    sList.printStudentList();

                }
            } while (pos == -1);
            do {
                vaccineID = MyToys.getAnInteger("Enter vaccine id: ", "Vaccine must be interger (1-99)", 1, 99);
                pos = vList.searchVaccineByID(vaccineID);
                if (pos != -1) {
                    System.out.println("Valid");
                } else {
                    System.out.println("Vaccine ID is invalid, please re_enter!!");
                    System.out.println("Vaccine list:");
                    vList.printVaccineList();
                }
            } while (pos == -1);
        } while (pos == -1);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        injectionDate1 = MyToys.getADate("Enter injection date (dd-MM-yyyy):",
                "Wrong valid date range(from 01-01-2021 to today )",
                df.parse("1-1-2021"), tomorrow);
        do {
            injectionPlace1 = pList.searchPlaceByName();
        } while (injectionPlace1 == null);
        injectionList.add(new Injection(id, injectionPlace1, injectionPlace2, injectionDate1, injectionDate2, studentId, vaccineID));
        System.out.println("A student's vaccine injection information is sucessfully added!");

    }

    // tao id ngau nhien
    private int generateID() {
        if (injectionList.isEmpty()) {
            return 1;
        }
        Collections.sort(injectionList);
        return injectionList.get(injectionList.size() - 1).getId() + 1;
    }

    //show ịnectionList
    public void showInjectionList() throws ParseException {
        if (injectionList.isEmpty()) {
            System.out.println("The list is empty. Nothing to print");
            return;
        }
        for (Injection injection : injectionList) {
            injection.showProfile();
            if (injection.getInjectionDate2() != null) {
                injection.showSecondInjection();
            } else {
                System.out.println("Second Vaccination is empty");
            }
        }
    }

    public void deleteInjection() {
        int pos, id;
        id = MyToys.getAnInteger("Input id to delete: ", "Injection ID can not be empty,"
                + " id must be positive interger", 1, Integer.MAX_VALUE);
        pos = searchInjectionByID(id);
        if (pos == -1) {
            System.err.println("Not found");
        } else {
            boolean c = false;
            System.out.println("Are you sure you want to delete?");
            c = menuYesNo();
            if (c == true) {
                injectionList.remove(pos);
                System.out.println("The Injection information is remove sucessfully");
            }
            return;
        }
    }

    // input data
    public void inputData() {
        sList.readFile();
        vList.readFile();
        pList.readFile();
        readFile();
    }

    public Injection checkInjectionByStudentid(String studentID) {
        for (Injection injection : injectionList) {
            if (studentID.equalsIgnoreCase(injection.getStudentId())) {
                return injection;
            }
        }
        return null;
    }

    public void updateInjection() throws ParseException {
        int pos, id;
        String injectionPlace2;
        Date injectionDate1, injectionDate2;
        do {
            id = MyToys.getAnInteger("Enter's Injection ID: ", "ID must be an positive "
                    + "integer", 1, Integer.MAX_VALUE);
            pos = searchInjectionByID(id);
            if (pos != -1) {
                System.out.println("Valid");
            } else {
                System.out.println("“Injection does not exist!");
                System.out.println("Injection list:");
                showInjectionList();
            }
        } while (pos == -1);
        Injection injec = searchInjection(id);
        if (injec.getInjectionPlace2() == null) {
            long milies = injec.getInjectionDate1().getTime();
            Date lowerBound = addDate(injec.getInjectionDate1(), 28);
            Date upperBound = addDate(injec.getInjectionDate1(), 84);
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

            injectionDate2 = MyToys.getADate("Enter injection date (dd-MM-yyyy):",
                    "the second injection must be given 4 to 12 weeks after the first injection",
                    lowerBound, upperBound);
            do {
                injectionPlace2 = pList.searchPlaceByName();
            } while (injectionPlace2 == null);
            searchInjection(id).setInjectionDate2(injectionDate2);
            searchInjection(id).setInjectionPlace2(injectionPlace2);
            System.out.println("A student's vaccine injection information is sucessfully added!");
        } else {
            System.out.println("Student has completed 2 injections!");
        }
    }

    public void searchInjectionByStudentID() throws ParseException {
        String studentID = MyToys.getID("Enter student's ID (AAXXXXXX): ",
                "The format of ID is AAXXXXXX", "^((SE)|(SS))\\d{6}$");
        Injection i = checkInjectionByStudentid(studentID);
        if (i != null) {
            i.showProfile();
        } else {
            System.out.println("Not found!");
        }
    }

    public void writeFile() {
        try {
            FileWriter fw = new FileWriter("injection.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            for (Injection injection : injectionList) {
                bw.write(injection.toString());
            }
            bw.close();
            fw.close();
        } catch (Exception e) {

        }
    }

    public ArrayList<Injection> readFile() {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        try {
            FileReader fr = new FileReader("injection.txt");
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                String[] txt = line.split(";");
                int id = Integer.parseInt(txt[0]);
                String studentId = txt[1];
                int vaccineID = Integer.parseInt(txt[2]);
                String injectionPlace1 = txt[3];
                Date injectionDate1 = (Date) formatter.parse(txt[4]);
                String injectionPlace2 = txt[5];
                Date injectionDate2 = (Date) formatter.parse(txt[6]);
                injectionList.add(new Injection(id, injectionPlace1, injectionPlace2, injectionDate1, injectionDate2, studentId, vaccineID));
            }

        } catch (Exception e) {
            System.out.println("loiiiiii");
        }
        return injectionList;
    }

    public boolean menuYesNo() {
        boolean flat = true;
        while (flat) {
            System.out.println("1.Yes");
            System.out.println("2.No");
            int choose;
            do {

                choose = MyToys.getAnInteger("Enter your choose:",
                        "Choice must be interger! ");
            } while (choose < 0 || choose > 2);
            switch (choose) {
                case 1: {
                    return true;
                }
                case 2: {
                    return false;
                }
            }
        }
        return flat;
    }

    public Date addDate(Date dt, int n) {
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, n);
        Date d = c.getTime();
        return d;
    }
}
