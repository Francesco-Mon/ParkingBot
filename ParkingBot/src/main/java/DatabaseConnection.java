import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClientSettings;
import com.mongodb.ConnectionString;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
public class DatabaseConnection {
    private MongoClient mongoClient;
    static MongoDatabase database;
    static TelegramLongPollingBot bot;

    public DatabaseConnection(String connectionString, String dbName, TelegramLongPollingBot bot) {
        this.bot = bot;
        ConnectionString connString = new ConnectionString(connectionString);
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connString).build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase(dbName);
    }
}