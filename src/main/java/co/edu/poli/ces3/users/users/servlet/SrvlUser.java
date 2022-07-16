package co.edu.poli.ces3.users.users.servlet;

import co.edu.poli.ces3.users.users.entities.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "SrvlUser", value = "/SrvlUser")
public class SrvlUser extends HttpServlet {
    private String message;
    private GsonBuilder gsonBuilder;
    private Gson gson;

    public SrvlUser(){
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    public static ArrayList<User> USERS = new ArrayList<>(Arrays.asList(
            new User(1, "Daniel","Rojas","dani786@gmail.com","DR786", "1234"),
            new User(2, " Maria Paulina","Balvin","balvinmp@outlook.com", "maripau","marpau15"),
            new User(3, "Rodrigo","Meneses","rdm1945@gmail.com", "rodrigodnofuturo", "qjer789.")
    ));

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        response.setContentType("application/json");
        this.setAccessControlHeaders(response);

        if(request.getParameter("username") == null) {
            out.print(gson.toJson(this.USERS));
        }else{
            User user = this.findUser(request.getParameter("username"));
            out.print(gson.toJson(user));
        }
        out.flush();
    }

    private User findUser(String username) {
        return this.USERS.stream()
                .filter(user -> user.getUsername().equals(username))
                .findAny()
                .orElse(null);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.setAccessControlHeaders(response);
        ServletOutputStream out = response.getOutputStream();
        response.setContentType("application/json");
        JsonObject body = this.getParamsFromPost(request);
        int max = USERS.size();
        User user = new User(
                Integer.valueOf(max+1),
                body.get("name").getAsString(),
                body.get("lastName").getAsString(),
                body.get("email").getAsString(),
                body.get("username").getAsString(),
                body.get("password").getAsString()
        );

        this.USERS.add(user);

        out.print(gson.toJson(user));
        out.flush();
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        response.setContentType("application/json");
        this.setAccessControlHeaders(response);

        if(request.getParameter("username") != null && USERS.isEmpty() == false) {
            User user = this.findUser(request.getParameter("username"));
            out.print("Usuario que se va a actualizar:");
            out.print(gson.toJson(user) + "\n");
            User editUser = this.editUser(request.getParameter("username"), request.getParameter("name"),request.getParameter("lastName"),request.getParameter("email"), request.getParameter("password"));

            out.print("Usuario actualizado "+ "\n");
            out.print(gson.toJson(editUser));
        }else{
            if(USERS.isEmpty()){
                out.print("La lista está vacia");
            }else{
                out.print("El usuario no existe");
            }
        }
        out.flush();
    }


    private User editUser(String username, String name, String lastName, String email, String password) {
        for(int i = 0; i < this.USERS.size(); i++){
            if(this.USERS.get(i).getUsername().equals(username)){
                this.USERS.get(i).setName(name);
                this.USERS.get(i).setLastName(lastName);
                this.USERS.get(i).setEmail(email);
                this.USERS.get(i).setPassword(password);
                return this.USERS.get(i);
            }
        }
        return null;
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        response.setContentType("application/json");
        this.setAccessControlHeaders(response);

        if(request.getParameter("username") != null && USERS.isEmpty() == false) {
            out.print(gson.toJson(this.USERS));
            User user = this.findUser(request.getParameter("username"));
            out.print("Usuario que se va a eliminar:");
            out.print(gson.toJson(user) + "\n");

            USERS.remove(USERS.size()-1);

            out.print("La lista después de borrar el usuario: "+ "\n");
            out.print(gson.toJson(this.USERS));
        }else{
            if(USERS.isEmpty()){
                out.print("La lista está vacia");
            }else{
                out.print("El usuario no existe");
            }
        }
        out.flush();
    }

    private JsonObject getParamsFromPost(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            sb.append(line + "\n");
            line = reader.readLine();
        }
        reader.close();
        return JsonParser.parseString(sb.toString()).getAsJsonObject();
    }

    private void setAccessControlHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, ´PUT, POST");
    }

    public void destroy() {
    }

}