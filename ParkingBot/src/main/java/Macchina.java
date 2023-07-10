import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
public class Macchina extends Veicolo {
    public Macchina(String targa) {
        super(targa);
    }
    public static void Entra(Update update, String targa) {
        MongoCollection<Document> targaCollection = DatabaseConnection.database.getCollection("parcheggiMacchine");
        MongoCollection<Document> decreaseCounter = DatabaseConnection.database.getCollection("AvailableParkMacchine");
        Document filter = new Document("targa", targa);
        long count = targaCollection.countDocuments(filter);
            if (count > 0) {
            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText("Targa già presente, riprova con un altra targa! 🚫");
            try {
                DatabaseConnection.bot.execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            Document veicoloDocument = new Document("targa", targa);
            targaCollection.insertOne(veicoloDocument);
            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText("Targa aggiunta, veicolo parcheggiato! ✅🚗");
            Document filter1 = new Document();
            Document update1 = new Document("$inc", new Document("contatoreMacchine", -1));
            decreaseCounter.updateOne(filter1, update1);
            try {
                DatabaseConnection.bot.execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    public static void Esci(Update update, String targa) {
        MongoCollection<Document> targaCollection = DatabaseConnection.database.getCollection("parcheggiMacchine");
        MongoCollection<Document> increaseCounter = DatabaseConnection.database.getCollection("AvailableParkMacchine");
        Document filter = new Document("targa", targa);
        long count = targaCollection.countDocuments(filter);
        if (count > 0) {
            Document veicoloDocument = new Document("targa", targa);
            targaCollection.deleteOne(veicoloDocument);
            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText("Macchina ritirata! 👋🏼🚗");
            Document filter1 = new Document();
            Document update1 = new Document("$inc", new Document("contatoreMacchine", 1));
            increaseCounter.updateOne(filter1, update1);
            try {
                DatabaseConnection.bot.execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText("Macchina NON presente nel parcheggio! 🚫🚗");
            try {
                DatabaseConnection.bot.execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}