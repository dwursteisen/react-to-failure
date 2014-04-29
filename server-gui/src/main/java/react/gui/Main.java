package react.gui;

import rx.Observable;

/**
 * User: david.wursteisen
 * Date: 29/04/14
 * Time: 18:17
 */
public class Main {

    public static void main(String[] args) {
        Observable.range(0, 10).last().subscribe((l) -> System.out.println("Helloworld !"));
    }
}
