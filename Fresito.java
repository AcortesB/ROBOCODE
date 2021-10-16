
package entrega1;

import robocode.AdvancedRobot;
import java.awt.Color;
import robocode.ScannedRobotEvent;
import robocode.HitWallEvent;
/**
 *
 * @author lluis & andrea
 */
public class Fresito extends AdvancedRobot {
    
    /* és una variable amb la que decidirem a quina direcció moure'ns quan haguem d'evitar quelcom obstacle 
    la fem així per a que vagi variant la direcció cada cop que esquivi, segons sigui positiva o negativa */
    int canviDireccio = -1;
    
    //numero que utilitzarem per establir una velocitat màxima random per variar-la
    double numeroRandom;
    
    //variable que guarda quant haurem de moure el canó per disparar al target
    double mocCanoTant;
    
    //la utilitzem per predir la posició en la que es trobarà l'enemic
    double angleAbsolutEnemic;
    
    //la utilitzem per predir la propera velocitat que portarà l'enemic
    double ultimaVelocitatEnemic;
    
    /***Radar gira fins que troba un objectiu. Moviment back&go constant. 
     * S'estableixen els colors del robot i que el cos, 
     * l'arma i el radar tinguin moviment independent entre ells.***/
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
    
    /***Valora si és a prop o lluny de l'objectiu i preveu conseqüentment la posició futura de l'enemic per disparar.
     * Fixa un valor random com a velocitat màxima del nostre robot en aquest torn (perque no sigui predictible).
     ***/
    public void onScannedRobot( ScannedRobotEvent e ) {

        angleAbsolutEnemic = e.getBearingRadians() + getHeadingRadians();
        //posicio de l'enemic respecte al nostre tanc + la direcció a la que estem mirant
        ultimaVelocitatEnemic = e.getVelocity() * Math.sin(e.getHeadingRadians() - angleAbsolutEnemic);
        //velocitat de l'enemic * sin(la direcció on està encarat l'enemic - (posicio de l'enemic respecte al nostre tank + la direcció a la que estem mirant)
        
        //li restem al gir del radar el tros que encara li quedava per fer
        setTurnRadarLeftRadians(getRadarTurnRemainingRadians());
        
        //Nota: maxima velocitat tanc robocode: 8 pixels/torn
        //amb un random entre 0.9 i 8 per a que no sigui predictable fixem un valor per a la velocitat màxima d'aquest torn
        numeroRandom = Math.random() * (8 - 0.9) + 0.9;
        setMaxVelocity((10*numeroRandom)+10);
        
        //si estem a menys o a 150 pixels de distància de l'enemic
        if ( e.getDistance() <= 150) {
            
            //preparem el canó
            apuntaCano(10);
            
            //ens posem perpendicular a l'enemic
            setTurnLeft(-90-e.getBearing());
            setAhead((e.getDistance() - 140)*canviDireccio);
            
            //disparem
            disparaSiCanoFred(e);
  
        }
        //si estem més lluny
        else{
            
            //preparem el canó
            apuntaCano(20);
            
            //ens apropem a la posició predita que ocuparà l'enemic
            setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(angleAbsolutEnemic-getHeadingRadians()+ultimaVelocitatEnemic/getVelocity()));
            setAhead((e.getDistance() - 140)*canviDireccio);
            
            //disparem
            disparaSiCanoFred(e);
        
        }
    
    }
    
    /***Dispara si el robot no té calent el canó. Dispara adaptant la potència del tir en base a la ditància a la que es trobi el robot enemic.***/
    public void disparaSiCanoFred( ScannedRobotEvent e ){
        //si no tenim el canó calent dispararem
            if ( getGunHeat() == 0 )
                //disparem adaptant la potència a la distancia a la que es trobi l'enemic
                setFire(Math.min(400 / e.getDistance(), 3));
                /*la màxima potencia a la que es pot disparar és 3, 
                però per si decás fem el minim entre la potencia calculada i 3
                */
    }
    
    /***Apunta el canó del robot on volguem disparar.Apunta una mica endavantat a la posició propera calculada de l'enemic.***/
    public void apuntaCano (int num){
        //apuntem el canó una mica endavantat de la posició calculada del nostre enemic
        mocCanoTant = robocode.util.Utils.normalRelativeAngle(angleAbsolutEnemic- getGunHeadingRadians()+ultimaVelocitatEnemic/num);
        setTurnGunRightRadians(mocCanoTant);
    }
    
    /***Fa moure el robot en direcció contrària a la paret amb la que ha xocat per allunyar-se d'ella.***/
    public void onHitWall( HitWallEvent e ){
        canviDireccio = canviDireccio * -1 ; //ho multipliquem per -1 per a que canvii el valor de la variable
    }
}
   
