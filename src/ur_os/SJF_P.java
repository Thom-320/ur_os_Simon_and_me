/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author prestamour
 */
public class SJF_P extends Scheduler{

    
    SJF_P(OS os){
        super(os);

    }
    
    @Override
    public void newProcess(boolean cpuEmpty){// When a NEW process enters the queue, process in CPU, if any, is extracted to compete with the rest
        if (!cpuEmpty) {
            os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, null);
        }
    } 

    @Override
    public void IOReturningProcess(boolean cpuEmpty){// When a process return from IO and enters the queue, process in CPU, if any, is extracted to compete with the rest
        if (!cpuEmpty) {
            os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, null);
        }
    } 
    
   
    @Override
    public void getNext(boolean cpuEmpty) {
        
        //Insert code here
        if (!cpuEmpty || processes.isEmpty()) return;

        Process best = null;
        int bestLen = Integer.MAX_VALUE;

        for (Process p : processes) {
            if (!p.isCurrentBurstCPU()) continue;
            int len = p.getRemainingTimeInCurrentBurst();
            if (len < bestLen) {
                best = p;
                bestLen = len;
            } else if (len == bestLen) {
                best = tieBreaker(best, p);
                bestLen = best.getRemainingTimeInCurrentBurst();
            }
        }

        if (best != null) {
            removeProcess(best);
            addContextSwitch();
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, best);
        }

     }
 
}
