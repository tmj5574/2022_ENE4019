import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

// 2021037156 탁민주 소켓 프로그래밍 과제
public class Server {
    // 여러 클라이언트, 즉 여러 소켓을 저장할 수 있는 Socket형 리스트를 선언한다. -> 멀티쓰레드 환경 구축
    static ArrayList<Socket> list = new ArrayList<Socket>();
    static Socket socket;

    public static void main(String[] args) {
    try {
        //서버가 시작됨
        ServerSocket sv_socket = new ServerSocket(8080);
        System.out.println("서버가 시작되었습니다.");



        while(true) {

            socket = sv_socket.accept();
            //소켓을 서버 소켓이 수락한 소켓으로 대입한다.
            System.out.println("서버에 연결중입니다.");
            list.add(socket);
            //클라이언트가 수락되면 해당 소켓을 list에 추가 후 쓰레드 진행
            //각각의 클라이언트에 대한 쓰레드
            /*1. 먼저 클라이언트의 최초 메세지(닉네임)을 받아 어떤 클라이언트가 접속하였는지 출력한다.
              2. 그 후에 메시지가 null이 아닐때까지 반복문을 돌린다. 그동안 해당 메세지를 list에 있는 모든 클라이언트에게
                출력한다.
              3. 만약 메세지가 'quit'라면  해당 클라이언트가 서버를 떠났음을 서버 및 모든 클라이언트에게 출력한다.
                 그 후에 리스트에서 해당 소켓은 삭제시킨다. */
            new Thread(new Runnable() {
                String UserID;
                @Override
                public void run() {
                    try {

                        String msg;

                        InputStream input = socket.getInputStream();
                        BufferedReader read = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                        PrintWriter wr = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                        UserID = read.readLine(); //소켓의 버퍼를 읽는다.
                        for (int i = 0; i < list.size(); i++) { // 리스트에 있는 소켓의 개수들만큼 접속 문구를 띄운다.
                            OutputStream out = list.get(i).getOutputStream();
                            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")), true);
                            writer.println(UserID + "님이 접속하셨습니다.");

                        }
                        System.out.println(UserID + "님이 접속하셨습니다."); //해당 서버창에 출력
                        //서버 -> 클라이언트
                        while ((msg = read.readLine()) != null) { //메세지가 null이 아닐때까지
                            if (msg.equals("quit")) {
                                wr.println(msg); //quit 이면 해당 메세지를 보낸 client의 창에만 출력
                                break;
                            }

                            System.out.println(msg);
                            for (int i = 0; i < list.size(); i++) {
                                //quit가 아닐 경우 모든 클라이언트에게 메세지 전송
                                OutputStream out = list.get(i).getOutputStream();
                                PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")), true);
                                writer.println(msg);

                            }
                        }


                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        System.out.println(UserID + "님이 방을 나갔습니다.");
                        for (int i = 0; i < list.size(); i++) {
                            OutputStream out;
                            // 만약 나갔을 경우 모든 클라이언트에게 퇴장 문구 전송.
                            try {
                                out = list.get(i).getOutputStream();
                                PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")), true);
                                writer.println(UserID + "님이 방을 나갔습니다.");
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    }
                }
            }

            ).start();
        }
    }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
