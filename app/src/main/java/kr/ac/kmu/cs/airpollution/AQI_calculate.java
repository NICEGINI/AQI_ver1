package kr.ac.kmu.cs.airpollution;

/**
 * Created by KCS on 2016-08-10.
 */
public class AQI_calculate {
    private static float CO_EIGHT_HR_AVE, SO2_ONE_HR_AVE, NO2_ONE_HR_AVE, PM_TWENTY_FOUR_HR_AVE, O3_EIGHT_HR_AVE,O3_ONE_HR_AVE;
    private static float CO_AQI, SO2_AQI, NO2_AQI, PM_AQI, O3_EIGHT_AQI, O3_ONE_AQI;
    private static final int BREAK_POINT_GOOD = 50, BREAK_POINT_MODERATE = 49, BREAK_POINT_UNHEALTHY_SG =49,
                                BREAK_POINT_UNHEALTHY = 49, BREAK_POINT_VERY_UNHEALTHY = 99, BREAK_POINT_HAZARDOUS = 99;

    private static final int GOOD_LOWER = 0, MODERATE_LOWER = 51, UNHEALTHY_SG_LOWER = 101,
            UNHEALTHY_LOWER = 151, VERY_UNHEALTHY_LOWER = 201, HAZARDOUS_LOWER = 301, HAZARDOUS_LOWER_SEC = 401, HAZARDOUS_MAX = 500 ;


    public static void setAirdata(float receive_CO, float receive_SO2, float receive_NO2, float receive_PM, float receive_O3){
        CO_EIGHT_HR_AVE = receive_CO;
        SO2_ONE_HR_AVE = receive_SO2;
        NO2_ONE_HR_AVE = receive_NO2;
        PM_TWENTY_FOUR_HR_AVE = receive_PM;
        O3_EIGHT_HR_AVE = receive_O3;
    }

    public static float CO_AQI_Cal(float data){
        // Break Point -> 4.4, 9.4, 12.4, 15.4, 30.4, 40.4, 50.4  measurement time is 8 hr
        if(CO_EIGHT_HR_AVE <= 4.4){
            return CO_AQI = (float)(((BREAK_POINT_GOOD)/(4.4 - 0.0))*(CO_EIGHT_HR_AVE - 0.0) + GOOD_LOWER);
        }
        else if(CO_EIGHT_HR_AVE <= 9.4){
            return CO_AQI = (float)(((BREAK_POINT_MODERATE)/(9.4 - 4.5))*(CO_EIGHT_HR_AVE - 4.5) + MODERATE_LOWER);
        }
        else if(CO_EIGHT_HR_AVE <= 12.4){
            return CO_AQI = (float)(((BREAK_POINT_UNHEALTHY_SG)/(12.4 - 9.5))*(CO_EIGHT_HR_AVE - 9.5) + UNHEALTHY_SG_LOWER);
        }
        else if(CO_EIGHT_HR_AVE <= 15.4){
            return CO_AQI = (float)(((BREAK_POINT_UNHEALTHY)/(15.4 - 12.5))*(CO_EIGHT_HR_AVE - 12.5) + UNHEALTHY_LOWER);
        }
        else if(CO_EIGHT_HR_AVE <= 30.4){
            return CO_AQI = (float)(((BREAK_POINT_VERY_UNHEALTHY)/(30.4 - 15.5))*(CO_EIGHT_HR_AVE - 15.5) + VERY_UNHEALTHY_LOWER);
        }
        else if(CO_EIGHT_HR_AVE <= 40.4){
            return CO_AQI = (float)(((BREAK_POINT_HAZARDOUS)/(40.4 - 30.5))*(CO_EIGHT_HR_AVE - 30.5) + HAZARDOUS_LOWER);
        }
        else if(CO_EIGHT_HR_AVE <= 50.4){
            return CO_AQI = (float)(((BREAK_POINT_HAZARDOUS)/(50.4 - 40.5))*(CO_EIGHT_HR_AVE - 40.5) + HAZARDOUS_LOWER_SEC);
        }
        else
            return CO_AQI = HAZARDOUS_MAX;
    }

    public static float SO2_AQI_Cal(float data){
        // Break Point -> 35, 75, 185, 304, 604, 804, 1004 measurement time is 1 hr
        if(SO2_ONE_HR_AVE <= 35){
            return SO2_AQI = (float)(((BREAK_POINT_GOOD)/(35 - 0.0))*(SO2_ONE_HR_AVE - 0.0) + GOOD_LOWER);
        }
        else if(SO2_ONE_HR_AVE <= 75){
            return SO2_AQI = (float)(((BREAK_POINT_MODERATE)/(75 - 36))*(SO2_ONE_HR_AVE - 36) + MODERATE_LOWER);
        }
        else if(SO2_ONE_HR_AVE <= 185){
            return SO2_AQI = (float)(((BREAK_POINT_UNHEALTHY_SG)/(185 - 76))*(SO2_ONE_HR_AVE - 76) + UNHEALTHY_SG_LOWER);
        }
        else if(SO2_ONE_HR_AVE <= 304){
            return SO2_AQI = (float)(((BREAK_POINT_UNHEALTHY)/(304 - 186))*(SO2_ONE_HR_AVE - 186) + UNHEALTHY_LOWER);
        }
        else if(SO2_ONE_HR_AVE <= 604){
            return SO2_AQI = (float)(((BREAK_POINT_VERY_UNHEALTHY)/(604 - 305))*(SO2_ONE_HR_AVE - 305) + VERY_UNHEALTHY_LOWER);
        }
        else if(SO2_ONE_HR_AVE <= 804){
            return SO2_AQI = (float)(((BREAK_POINT_HAZARDOUS)/(804 - 605))*(SO2_ONE_HR_AVE - 605) + HAZARDOUS_LOWER);
        }
        else if(SO2_ONE_HR_AVE <= 1004){
            return SO2_AQI = (float)(((BREAK_POINT_HAZARDOUS)/(1004 - 805))*(SO2_ONE_HR_AVE - 805) + HAZARDOUS_LOWER_SEC);
        }
        else
            return SO2_AQI = HAZARDOUS_MAX;
    }

    public static float NO2_AQI_Cal(float data){
        // Break Point -> 53, 100, 360, 649, 1249, 1649, 2049  measurement time is 1 hr
        if(NO2_ONE_HR_AVE <= 53){
            return NO2_AQI = (float)(((BREAK_POINT_GOOD)/(53 - 0.0))*(SO2_ONE_HR_AVE - 0.0) + GOOD_LOWER);
        }
        else if(NO2_ONE_HR_AVE <= 100){
            return NO2_AQI = (float)(((BREAK_POINT_MODERATE)/(100 - 53))*(NO2_ONE_HR_AVE - 53) + MODERATE_LOWER);
        }
        else if(NO2_ONE_HR_AVE <= 360){
            return NO2_AQI = (float)(((BREAK_POINT_UNHEALTHY_SG)/(360 - 101))*(NO2_ONE_HR_AVE - 101) + UNHEALTHY_SG_LOWER);
        }
        else if(NO2_ONE_HR_AVE <= 649){
            return NO2_AQI = (float)(((BREAK_POINT_UNHEALTHY)/(649 - 361))*(NO2_ONE_HR_AVE - 361) + UNHEALTHY_LOWER);
        }
        else if(NO2_ONE_HR_AVE <= 1249){
            return NO2_AQI = (float)(((BREAK_POINT_VERY_UNHEALTHY)/(1249 - 650))*(NO2_ONE_HR_AVE - 650) + VERY_UNHEALTHY_LOWER);
        }
        else if(NO2_ONE_HR_AVE <= 1649){
            return NO2_AQI = (float)(((BREAK_POINT_HAZARDOUS)/(1649 - 1250))*(NO2_ONE_HR_AVE - 1250) + HAZARDOUS_LOWER);
        }
        else if(NO2_ONE_HR_AVE <= 2049){
            return NO2_AQI = (float)(((BREAK_POINT_HAZARDOUS)/(2049 - 1650))*(NO2_ONE_HR_AVE - 1650) + HAZARDOUS_LOWER_SEC);
        }
        else
            return NO2_AQI = HAZARDOUS_MAX;
    }

    public static float PM_AQI_Cal(float data){
        // Break Point -> 12, 35.4, 55.4, 150.4, 250.4, 350.4, 500.4  measurement time is 1 hr
        if(PM_TWENTY_FOUR_HR_AVE <= 12.0){
            return PM_AQI = (float)(((BREAK_POINT_GOOD)/(12.0 - 0.0))*(PM_TWENTY_FOUR_HR_AVE - 0.0) + GOOD_LOWER);
        }
        else if(PM_TWENTY_FOUR_HR_AVE <= 35.4){
            return PM_AQI = (float)(((BREAK_POINT_MODERATE)/(35.4 - 12.1))*(PM_TWENTY_FOUR_HR_AVE - 12.1) + MODERATE_LOWER);
        }
        else if(PM_TWENTY_FOUR_HR_AVE <= 55.4){
            return PM_AQI = (float)(((BREAK_POINT_UNHEALTHY_SG)/(55.4 - 35.5))*(PM_TWENTY_FOUR_HR_AVE - 35.5) + UNHEALTHY_SG_LOWER);
        }
        else if(PM_TWENTY_FOUR_HR_AVE <= 150.4){
            return PM_AQI = (float)(((BREAK_POINT_UNHEALTHY)/(150.4 - 55.5))*(PM_TWENTY_FOUR_HR_AVE - 55.5) + UNHEALTHY_LOWER);
        }
        else if(PM_TWENTY_FOUR_HR_AVE <= 250.4){
            return PM_AQI = (float)(((BREAK_POINT_VERY_UNHEALTHY)/(250.4 - 150.5))*(PM_TWENTY_FOUR_HR_AVE - 150.5) + VERY_UNHEALTHY_LOWER);
        }
        else if(PM_TWENTY_FOUR_HR_AVE <= 350.4){
            return PM_AQI = (float)(((BREAK_POINT_HAZARDOUS)/(350.4 - 250.5))*(PM_TWENTY_FOUR_HR_AVE - 250.5) + HAZARDOUS_LOWER);
        }
        else if(PM_TWENTY_FOUR_HR_AVE <= 500.4){
            return PM_AQI = (float)(((BREAK_POINT_HAZARDOUS)/(500.4 - 350.5))*(PM_TWENTY_FOUR_HR_AVE - 350.5) + HAZARDOUS_LOWER_SEC);
        }
        else
            return PM_AQI = HAZARDOUS_MAX;
    }

    public static float O3_EIGHT_AQI_Cal(float data){
        // Break Point -> 54, 70, 85, 105, 200  measurement time is 8 hr not exist Hazardous area
        if(O3_EIGHT_HR_AVE <= 54){
            return O3_EIGHT_AQI = (float)(((BREAK_POINT_GOOD)/(54 - 0.0))*(O3_EIGHT_HR_AVE - 0.0) + GOOD_LOWER);
        }
        else if(O3_EIGHT_HR_AVE <= 70){
            return O3_EIGHT_AQI = (float)(((BREAK_POINT_MODERATE)/(70 - 55))*(O3_EIGHT_HR_AVE - 55) + MODERATE_LOWER);
        }
        else if(O3_EIGHT_HR_AVE <= 85){
            return O3_EIGHT_AQI = (float)(((BREAK_POINT_UNHEALTHY_SG)/(85 - 71))*(O3_EIGHT_HR_AVE - 71) + UNHEALTHY_SG_LOWER);
        }
        else if(O3_EIGHT_HR_AVE <= 105){
            return  O3_EIGHT_AQI = (float)(((BREAK_POINT_UNHEALTHY)/(105 - 86))*(O3_EIGHT_HR_AVE - 86) + UNHEALTHY_LOWER);
        }
        else if(O3_EIGHT_HR_AVE <= 200){
            return O3_EIGHT_AQI = (float)(((BREAK_POINT_VERY_UNHEALTHY)/(200 - 106))*(O3_EIGHT_HR_AVE - 106) + VERY_UNHEALTHY_LOWER);
        }
        else
        {
            if(O3_ONE_HR_AVE <= 504){
                return O3_ONE_AQI = (float)(((BREAK_POINT_VERY_UNHEALTHY)/(504 - 405))*(O3_ONE_HR_AVE - 405) + HAZARDOUS_LOWER);
            }
            else if(O3_ONE_HR_AVE <= 604){
                return O3_ONE_AQI = (float)(((BREAK_POINT_VERY_UNHEALTHY)/(604 - 505))*(O3_ONE_HR_AVE - 505) + HAZARDOUS_LOWER_SEC);
            }
            else
                return O3_ONE_AQI = HAZARDOUS_MAX;
            }
    }

    public static float O3_ONE_AQI_Cal(float data){
        // Break Point -> 164, 204, 404, 504, 604  measurement time is 1 hr not exist Good area
        if(O3_ONE_HR_AVE <= 124){
            if(O3_EIGHT_HR_AVE <= 54)
                return O3_EIGHT_AQI = (float)(((BREAK_POINT_GOOD)/(54 - 0.0))*(O3_EIGHT_HR_AVE - 0.0) + GOOD_LOWER);
            else if(O3_EIGHT_HR_AVE <=70)
                return O3_EIGHT_AQI = (float)(((BREAK_POINT_MODERATE)/(70 - 55))*(O3_EIGHT_HR_AVE - 55) + MODERATE_LOWER);
        }
        else if(O3_ONE_HR_AVE <= 164){
            return O3_ONE_AQI = (float)(((BREAK_POINT_MODERATE)/(164 - 125))*(O3_ONE_HR_AVE - 125) + UNHEALTHY_SG_LOWER);
        }
        else if(O3_ONE_HR_AVE <= 204){
            return O3_ONE_AQI = (float)(((BREAK_POINT_UNHEALTHY_SG)/(204 - 165))*(O3_ONE_HR_AVE - 165) + UNHEALTHY_LOWER);
        }
        else if(O3_ONE_HR_AVE <= 404){
            return O3_ONE_AQI = (float)(((BREAK_POINT_UNHEALTHY)/(404 - 205))*(O3_ONE_HR_AVE - 205) + VERY_UNHEALTHY_LOWER);
        }
        else if(O3_ONE_HR_AVE <= 504){
            return O3_ONE_AQI = (float)(((BREAK_POINT_VERY_UNHEALTHY)/(504 - 405))*(O3_ONE_HR_AVE - 405) + HAZARDOUS_LOWER);
        }
        else if(O3_ONE_HR_AVE <= 604){
            return O3_ONE_AQI = (float)(((BREAK_POINT_HAZARDOUS)/(604 - 505))*(O3_ONE_HR_AVE - 505) + HAZARDOUS_LOWER_SEC);
        }
        else {
            return O3_ONE_AQI = HAZARDOUS_MAX;
        }
        return O3_ONE_AQI;
    }
}
