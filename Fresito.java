/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotEnPruebas;

import robocode.AdvancedRobot;
import java.awt.Color;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robocode.HitWallEvent;
/**
 *
 * @author andrea
 */
public class Fresito extends AdvancedRobot {
    
    //és una variable amb la que decidirem a quina direcció moure'ns quan haguem d'evitar quelcom obstacle 
    //la fem així per a que vagi variant la direcció cada cop que esquivi, segons sigui positiva o negativa
    int canviDireccio = -1;
    
    //numero que utilitzarem per establir una velocitat màxima random per variar-la
    double numeroRandom;
    
    //variable que guarda quant haurem de moure el canó per disparar al target
    double mocCanoTant;
    
    double absBearing; //TODO coment enemies absolute bearing
    
    
    double latVel; //TODO coment enemies later velocity
    

    public void run() {
        
        //li posem els colors que volguem
        setColors(Color.red, Color.black, Color.green); // cos, arma, radar
        
        //per poder moure el radar i el cos del tank per separat
        setAdjustRadarForRobotTurn(true);
        //per poder moure el cano i el cos del tank per separat
        setAdjustGunForRobotTurn(true);
        
        //fem girar el radar cap a l'esquerra quan no trobi un objetiu ( així el busca )
        turnRadarRightRadians(Double.POSITIVE_INFINITY);
        
        //moviment inicial de quan vam començar a fer proves
        ahead(200);
        back(200);
    }

    public void onScannedRobot( ScannedRobotEvent e ) {
        

        absBearing = e.getBearingRadians() + getHeadingRadians();
        latVel = e.getVelocity() * Math.sin(e.getHeadingRadians() -absBearing);
        
        //TODO coment
        setTurnRadarLeftRadians(getRadarTurnRemainingRadians());
        
        //amb un random entre 9 i 15 per a que no sigui predictable fixem un valor per a la velocitat màxima d'aquest torn
        //TODO: velocitat maxima en el robocode?
        numeroRandom = Math.random() * (15 - 9) + 9;
        setMaxVelocity((10*numeroRandom)+10); //TODO: et sembla bé que sigui 10? posava 12, però no varia el num de 1sts
        
        //si estem a menys o a 150 pixels de distància de l'enemic
        if ( e.getDistance() <= 150) {
            
            //preparem el canó
            apuntaCano(15);
            
            //ens posem perpendicular a l'enemic
            setTurnLeft(-90-e.getBearing());
            setAhead((e.getDistance() - 140)*canviDireccio); //TODO
            
            //disparem
            disparaSiCanoFred(e);
  
        }
        //si estem més lluny
        else{
            
            //preparem el canó
            apuntaCano(22);
            
            //TODO coment
            setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(absBearing-getHeadingRadians()+latVel/getVelocity()));//drive towards the enemies predicted future location
            setAhead((e.getDistance() - 140)*canviDireccio);
            
            //disparem
            disparaSiCanoFred(e);
        
        }
    
    }
    
    //funció per disparar si tenim el canó fred
    public void disparaSiCanoFred( ScannedRobotEvent e ){
        //si no tenim el canó calent dispararem
            if ( getGunHeat() == 0 )
                //disparem adaptant la potència a la distancia a la que es trobi l'enemic
                setFire(Math.min(400 / e.getDistance(), 3));
                /*la màxima potencia a la que es pot disparar és 3, 
                però per si decás fem el minim entre la potencia calculada i 3
                */
    }
    
    //funció per apuntar el canó on volguem disparar
    public void apuntaCano (int num){
        mocCanoTant = robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/num);//amount to turn our gun, lead just a little bit
        setTurnGunRightRadians(mocCanoTant); //turn our gun
    }
    
    //si xoquem contra una paret 
    public void onHitWall( HitWallEvent e ){
        //ens mourem en direcció contraria a la paret per allunyar-no d'ella
        canviDireccio = canviDireccio * -1 ; //ho multipliquem per -1 per a que canvii el valor de la variable
    }
}
