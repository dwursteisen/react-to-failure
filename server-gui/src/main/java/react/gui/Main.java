package react.gui;

import java.net.URI;
import java.net.URISyntaxException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;
import org.webbitserver.netty.WebSocketClient;
import rx.Observable;
import rx.Subscriber;

/**
 * User: david.wursteisen
 * Date: 29/04/14
 * Time: 18:17
 */
public class Main extends Application {

    public static void main(String[] args) throws URISyntaxException {
        new WebSocketClient(new URI("ws://localhost:4444/context/"), new BaseWebSocketHandler() {
            @Override
            public void onMessage(final WebSocketConnection connection, final String msg) throws Throwable {
                System.out.println("Received => " + msg);
            }
        }).start();
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        from(btn).subscribe((evt) -> System.out.println("Hello World"));

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }


    public static Observable<ActionEvent> from(Button button) {
        return Observable.create(new Observable.OnSubscribe<ActionEvent>() {
            @Override
            public void call(final Subscriber<? super ActionEvent> subscriber) {
                button.setOnAction((evt) -> {
                    if(!subscriber.isUnsubscribed()) {
                        subscriber.onNext(evt);
                    }
                });
            }
        });
    }
}
