package bettersiege;

//All of the data for a catapult and the things to be done to it
public class Catapult {
    int health = 100;
    int charge = 0;
    boolean isDestroyed = false;
    boolean isEnded = false;
    
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
    void resetCharge() {
        charge = 0;
    }
    int addCharge() {
        charge = charge + 4;
        if(charge < 20) charge = 20;
        return charge;
    }
    boolean isCharged() {
        if(charge == 20) return true;
        return false;
    }
    void end() {
        isEnded = true;
    }
    boolean isEnded() {
        return isEnded;
    }
}
