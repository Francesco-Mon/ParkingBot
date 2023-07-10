import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.Random;
public class RandomThread extends Thread {
    private boolean isRunning = true;
    private final ParkingBot bot;
    private final Update update;
    public RandomThread(ParkingBot bot,Update update){
        this.bot = bot;
        this.update = update;
    }

    @Override
    public void run() {
        Random random = new Random();
        if(isRunning){
            try {
                Thread.sleep(random.nextInt(86400000) + 1000); // Attendi tra 1 secondo e 24 ore
                String message = "Ci sono parcheggi disponibili!";
                SendMessage risposta = new SendMessage();
                risposta.setChatId(update.getMessage().getChatId().toString());
                risposta.setText(message);
                try {
                    bot.execute(risposta);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isRunning = false;
        }
    }
}