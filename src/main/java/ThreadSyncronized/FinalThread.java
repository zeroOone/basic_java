package ThreadSyncronized;

import java.util.Set;
import java.util.TreeSet;

/**
 * 使用final创建一个不可变的线程安全类
 * final ： 不可变关键字 —— 不允许状态改变
 */
public class FinalThread {
    private final Set<String> planets = new TreeSet<>();

    public FinalThread() {
        planets.add("Mercury");
        planets.add("Venus");
        planets.add("Earth");
        planets.add("Mars");
        planets.add("Jupiter");
        planets.add("Saturn");
        planets.add("Uranus");
        planets.add("Neptune");
    }

   public boolean isPlanet(String planetName) {
        return planets.contains(planetName);
   }

}
