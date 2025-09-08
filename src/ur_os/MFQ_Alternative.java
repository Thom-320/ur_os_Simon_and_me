/*
 * Alternative MFQ implementation using child schedulers as suggested in skeleton
 * This shows how the skeleton's suggestion could be implemented
 */
package ur_os;

import java.util.ArrayList;
import java.util.Arrays;

public class MFQ_Alternative extends Scheduler{

    int currentScheduler;
    
    private ArrayList<Scheduler> schedulers;
    
    // Track which level each process is at for demotion logic
    private java.util.Map<Process, Integer> processLevels = new java.util.HashMap<>();
    
    MFQ_Alternative(OS os){
        super(os);
        currentScheduler = -1;
        schedulers = new ArrayList();
    }
    
    MFQ_Alternative(OS os, Scheduler... s){ //Received multiple arrays
        this(os);
        schedulers.addAll(Arrays.asList(s));
        if(s.length > 0)
            currentScheduler = 0;
    }
        
    @Override
    public void addProcess(Process p){
       //Overwriting the parent's addProcess(Process p) method may be necessary in order to decide what to do with process coming from the CPU.
        if (p == null) return;
        
        int targetLevel;
        if (p.getState() == ProcessState.CPU) {
            // Process was preempted - demote it
            Integer currentLevel = processLevels.get(p);
            if (currentLevel != null) {
                targetLevel = Math.min(currentLevel + 1, schedulers.size() - 1);
            } else {
                targetLevel = 0; // fallback
            }
        } else {
            // New process or IO return - goes to top queue
            targetLevel = 0;
        }
        
        p.setState(ProcessState.READY);
        p.setCurrentScheduler(targetLevel);
        processLevels.put(p, targetLevel);
        schedulers.get(targetLevel).addProcess(p);
    }
    
    void defineCurrentScheduler(){
        //This method is suggested to help you find the scheduler that should be the next in line to provide processes... perhaps the one with process in the queue?
        currentScheduler = -1;
        for (int i = 0; i < schedulers.size(); i++) {
            if (!schedulers.get(i).isEmpty()) {
                currentScheduler = i;
                return;
            }
        }
    }
    
   
    @Override
    public void getNext(boolean cpuEmpty) {
        //Suggestion: now that you know on which scheduler a process is, you need to keep advancing that scheduler. If it a preemptive one, you need to notice the changes
        //that it may have caused and verify if the change is coherent with the priority policy for the queues.
        
        defineCurrentScheduler();
        
        if (cpuEmpty) {
            if (currentScheduler >= 0) {
                schedulers.get(currentScheduler).getNext(true);
            }
            return;
        }
        
        // CPU is busy - let the current scheduler handle it
        Process running = os.getProcessInCPU();
        if (running != null) {
            Integer level = processLevels.get(running);
            if (level != null && level < schedulers.size()) {
                schedulers.get(level).getNext(false);
            }
        }
    }
    
    @Override
    public void newProcess(boolean cpuEmpty) {} //Non-preemtive in this event

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {} //Non-preemtive in this event
    
    @Override
    public int getTotalContextSwitches() {
        int total = super.getTotalContextSwitches();
        for (Scheduler s : schedulers) {
            total += s.getTotalContextSwitches();
        }
        return total;
    }
}
