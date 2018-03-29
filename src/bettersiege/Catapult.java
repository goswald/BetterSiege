package bettersiege;

//All of the data for a catapult and the things to be done to it
public class Catapult {
    int health = 100;
    int charge = 0;
    boolean isDestroyed = false;
    
    void fetchData() {
        
    }
    boolean addDamage(int amount) {
        health = health - amount;
        if(health <= 0) return true;
        return false;
    }
    int addHealth(int amount) {
        health = health + amount;
        if(health < 100) health = 100;
        return health;
    }
    int getHealth() {
        return health;
    }
    boolean addCharge() {
        charge = charge + 1;
        if(charge == 20) return true;
        return false;
    }
    boolean isCharged() {
        if(charge == 20) return true;
        return false;
    }
}
