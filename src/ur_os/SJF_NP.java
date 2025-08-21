/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

import java.lang.Process;

/**
 *
 * @author prestamour
 */
public class SJF_NP extends Scheduler{

    
    SJF_NP(OS os){
        super(os);
    }
    
   
    @Override
    public void getNext(boolean cpuEmpty) {

       //Insert code here
        if (!processes.isEmpty() && cpuEmpty)
        {
            ur_os.Process best = null;
            int bestLen = Integer.MAX_VALUE;

            for (Process proc : processes) {
                if (!proc.isCurrentBurstCPU()) continue;          // Skip si el siguiente procces es I/O.
                int len = proc.getRemainingTimeInCurrentBurst();  // Tiempo exacto que le queda al CPU burst actual.
                if (len < bestLen) {
                    best = proc;
                    bestLen = len;
                } else if (len == bestLen) {
                    best = tieBreaker(best, proc);                // inherited deterministic tie resolver
                }
            }
        }
        
        if (best != null) {
            removeProcess(best);
            addContextSwitch();
            os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, best);
        }
    }


    
    @Override
    public void newProcess(boolean cpuEmpty) {} //Non-preemtive

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {} //Non-preemtive
    
}
