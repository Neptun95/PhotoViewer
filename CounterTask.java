package sample;

import javafx.scene.image.Image;
import java.util.concurrent.atomic.AtomicBoolean;

public class CounterTask implements Runnable {
    final  AtomicBoolean suspend = new AtomicBoolean();
    final AtomicBoolean stop = new AtomicBoolean();

    CounterTask(String name){
        suspend.set(false);
        stop.set(false);
    }
    @Override
    public void run() {

                try{
                    for (int i = 0 ; i < Main.pics.size(); i++){
                        Main.screen.setImage(new Image(Main.pics.get(i)));
                        Thread.sleep(Main.sleep*1000);
                        synchronized (this){
                            while (suspend.get()){
                                wait();

                            }
                            if(stop.get())
                                break;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("Something's  wrong!!!");
                }
                Main.stop.setVisible(false);
                Main.pause.setVisible(false);

    }
    synchronized void stop(){
        stop.set(true);
        suspend.set(false);
        notify();
    }
    synchronized void setSuspend(){

        suspend.set(true);
    }

    synchronized void setResume(){
        suspend.set(false);
        notify();
    }
}
