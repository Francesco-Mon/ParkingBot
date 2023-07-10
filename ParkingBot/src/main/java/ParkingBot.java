import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ParkingBot extends TelegramLongPollingBot {
    private final DatabaseConnection dbConnection;
    private int contatoreMacchine;
    private int contatoreMotorini;
    public ParkingBot(){
        dbConnection = new DatabaseConnection("mongodb://localhost:27017", "ParkingDatabase",this); //connessione al database
        contatoreMacchine = getContatoreMacchineFromDatabase();
        contatoreMotorini = getContatoreMotoriniFromDatabase();
    }
    private int getContatoreMacchineFromDatabase() {
        MongoCollection<Document> decreaseCounter = DatabaseConnection.database.getCollection("AvailableParkMacchine");
        Document counterDocument = decreaseCounter.find().first();
        return counterDocument.getInteger("contatoreMacchine", 0);
    }
    private int getContatoreMotoriniFromDatabase() {
        MongoCollection<Document> decreaseCounter = DatabaseConnection.database.getCollection("AvailableParkMotorini");
        Document counterDocument = decreaseCounter.find().first();
        return counterDocument.getInteger("contatoreMotorini", 0);
    }
    @Override
    public void onUpdateReceived(Update update) {
        RandomThread randomthread = new RandomThread(this,update);
        randomthread.start();
        String nomeUtente = update.getMessage().getFrom().getFirstName(); // Prende il nome dall'account Telegram
        String cognomeUtente = update.getMessage().getFrom().getLastName(); // Prende il cognome dall'account Telegram
        System.out.println("Utente: " + nomeUtente + " " + cognomeUtente); // Stampa nel terminale il nome e cognome dell'utente
        System.out.println("Input: " + update.getMessage().getText()); // Stampa nel terminale l'input inserito dall'utente
        String input = update.getMessage().getText().toLowerCase(); // Prende l'input dell'utente e lo salva nella string "command"
        // prendo il comando "/comando veicolo targa", e divido in 3 variabili
        String[] parts = input.split("\\s+"); // Dividi la stringa in base allo spazio
        int wordCount = parts.length;
        if (wordCount==1){
            if (input.equalsIgnoreCase("/start")){
                String message = ", bevenuto su ParkingBot!🎉\n\n" +
                        "I comandi disponibili sono:\n" +
                        "/start - Avvia ParkingBot\n\n" +
                        "/postidisponibili - Mostra quanti parcheggi disponibili ci sono\n\n" +
                        "/parcheggia - Utilizza il formato:\n/parcheggia macchina/motorino targa️\n\n" +
                        "/esci - Utilizza il formato:\n/esci macchina/motorino targa";
                SendMessage risposta = new SendMessage();
                risposta.setChatId(update.getMessage().getChatId().toString());
                risposta.setText("Ciao " + nomeUtente + message);
                try {
                    execute(risposta);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if (input.equalsIgnoreCase("/postidisponibili")){
                contatoreMotorini=getContatoreMotoriniFromDatabase();
                contatoreMacchine=getContatoreMacchineFromDatabase();
                String message6 = "Ci sono "+ contatoreMacchine + " posti auto disponibili🆓🚗\nCi sono "+ contatoreMotorini + " posti moto disponibili🆓🛵";
                SendMessage risposta6 = new SendMessage();
                risposta6.setChatId(update.getMessage().getChatId().toString());
                risposta6.setText("Ciao " + nomeUtente + "\n" + message6);
                try {
                    execute(risposta6);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if(input.equalsIgnoreCase("/parcheggia")){
                String message8 = "Utilizza il seguente formato:\n/parcheggia macchina/motorino targa";
                SendMessage risposta8 = new SendMessage();
                risposta8.setChatId(update.getMessage().getChatId().toString());
                risposta8.setText(message8);
                try {
                    execute(risposta8);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else if(input.equalsIgnoreCase("/esci")){
                String message9 = "Utilizza il seguente formato:\n/esci macchina/motorino targa";
                SendMessage risposta9 = new SendMessage();
                risposta9.setChatId(update.getMessage().getChatId().toString());
                risposta9.setText(message9);
                try {
                    execute(risposta9);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else {
                String message7 = "Comando non riconosciuto\nI comandi disponibili sono:\n" +
                        "/start - Avvia ParkingBot\n\n" +
                        "/postidisponibili - Mostra quanti parcheggi disponibili ci sono\n\n" +
                        "/parcheggia - Utilizza il formato:\n/parcheggia macchina/motorino targa\n\n" +
                        "/esci - Utilizza il formato:\n/esci macchina/motorino targa";
                SendMessage risposta7 = new SendMessage();
                risposta7.setChatId(update.getMessage().getChatId().toString());
                risposta7.setText(message7);
                try {
                    execute(risposta7);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }else if (wordCount==3){
            String comando = parts[0]; // Prima parola dopo il comando "/"
            String veicolo = parts[1]; // Seconda parola
            String targa = parts[2]; // Terza parola
        if (comando.equalsIgnoreCase("/parcheggia")){
            if (veicolo.equalsIgnoreCase("macchina")){
                contatoreMacchine=getContatoreMacchineFromDatabase();
                if(contatoreMacchine>0) {
                    System.out.println("targa inserita: " + targa);
                    Macchina.Entra(update, targa);
                }else{
                    String message1 = "Il parcheggio per le macchine è pieno! 🚫";
                    SendMessage risposta1 = new SendMessage();
                    risposta1.setChatId(update.getMessage().getChatId().toString());
                    risposta1.setText(message1);
                    try {
                        execute(risposta1);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }else if (veicolo.equalsIgnoreCase("motorino")){
                contatoreMotorini=getContatoreMotoriniFromDatabase();
                if (contatoreMotorini>0) {
                    System.out.println("targa inserita: " + targa);
                    Motorino.Entra(update, targa);
                }else{
                    String message2 = "Il parcheggio per i motorini è pieno! 🚫";
                    SendMessage risposta2 = new SendMessage();
                    risposta2.setChatId(update.getMessage().getChatId().toString());
                    risposta2.setText(message2);
                    try {
                        execute(risposta2);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                String message4 = "Veicolo non riconosciuto, riprovare.";
                SendMessage rispostaNonValida = new SendMessage();
                rispostaNonValida.setChatId(update.getMessage().getChatId().toString());
                rispostaNonValida.setText(message4);
                try {
                    execute(rispostaNonValida); // Invia la risposta
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }else if (comando.equalsIgnoreCase("/esci")){
            if (veicolo.equalsIgnoreCase("macchina")){
                contatoreMacchine=getContatoreMacchineFromDatabase();
                if(contatoreMacchine<5) {
                    System.out.println("targa inserita: " + targa);
                    Macchina.Esci(update, targa);
                }else {
                    String message4 = "Il parcheggio delle macchine è vuoto! 🆓";
                    SendMessage risposta4 = new SendMessage();
                    risposta4.setChatId(update.getMessage().getChatId().toString());
                    risposta4.setText(message4);
                    try {
                        execute(risposta4);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }else if (veicolo.equalsIgnoreCase("motorino")){
                contatoreMotorini=getContatoreMotoriniFromDatabase();
                if(contatoreMotorini<10) {
                    System.out.println("targa inserita: " + targa);
                    Motorino.Esci(update, targa);
                }else{
                    String message5 = "Il parcheggio dei motorini è vuoto! 🆓";
                    SendMessage risposta5 = new SendMessage();
                    risposta5.setChatId(update.getMessage().getChatId().toString());
                    risposta5.setText(message5);
                    try {
                        execute(risposta5);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                String message3 = "Veicolo non riconosciuto, riprovare.";
                SendMessage rispostaNonValida = new SendMessage();
                rispostaNonValida.setChatId(update.getMessage().getChatId().toString());
                rispostaNonValida.setText(message3);
                try {
                    execute(rispostaNonValida); // Invia la risposta
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }else{
            String message11 = "Comando non riconosciuto\nI comandi disponibili sono:\n" +
                    "/start - Avvia ParkingBot\n\n" +
                    "/postidisponibili - Mostra quanti parcheggi disponibili ci sono\n\n" +
                    "/parcheggia - Utilizza il formato:\n/parcheggia macchina/motorino targa\n\n" +
                    "/esci - Utilizza il formato:\n/esci macchina/motorino targa";
            SendMessage risposta11 = new SendMessage();
            risposta11.setChatId(update.getMessage().getChatId().toString());
            risposta11.setText(message11);
            try {
                execute(risposta11);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        }else{
            String message10 = "Comando non riconosciuto\nI comandi disponibili sono:\n" +
                    "/start - Avvia ParkingBot\n\n" +
                    "/postidisponibili - Mostra quanti parcheggi disponibili ci sono\n\n" +
                    "/parcheggia - Utilizza il formato:\n/parcheggia macchina/motorino targa\n\n" +
                    "/esci - Utilizza il formato:\n/esci macchina/motorino targa";
            SendMessage risposta10 = new SendMessage();
            risposta10.setChatId(update.getMessage().getChatId().toString());
            risposta10.setText(message10);
            try {
                execute(risposta10);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public String getBotUsername() {
        return "ParkingManagementBot";
    }
    @Override
    public String getBotToken() {
        return "6092028575:AAHjeSID1fc6CM7rsS6cKP7pBXsRAzpzIHc";
    }
}