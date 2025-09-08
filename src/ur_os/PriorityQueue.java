/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os;

import java.util.ArrayList;
import java.util.Arrays;


/**
 *
 * @author prestamour
 */
public class PriorityQueue extends Scheduler{

    int currentScheduler;
    
    private ArrayList<Scheduler> schedulers;
    
    PriorityQueue(OS os){
        super(os);
        currentScheduler = -1;
        schedulers = new ArrayList();
    }
    
    PriorityQueue(OS os, Scheduler... s){ //Received multiple arrays
        this(os);
        schedulers.addAll(Arrays.asList(s));
        if(s.length > 0)
            currentScheduler = 0;
    }
    
    
    @Override
    public void addProcess(Process p){
       //Overwriting the parent's addProcess(Process p) method may be necessary in order to decide what to do with process coming from the CPU.
       //On which queue should the process go?
        if (p == null) return;
        
        int target;
        if (p.getState() == ProcessState.CPU) {
            target = p.getCurrentScheduler();
        } else {
            target = p.getPriority();
        }
        
        if (target < 0) target = 0;
        if (target >= schedulers.size()) target = schedulers.size() - 1;
        
        p.setState(ProcessState.READY);
        p.setCurrentScheduler(target);
        schedulers.get(target).addProcess(p);
    }
    
    void defineCurrentScheduler(){
        //This methos is suggested to help you find the scheduler that should be the next in line to provide processes... perhaps the one with process in the queue?
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
        //Suggestion: if the CPU is empty, just find the next scheduler based on the order and the existence of processes
        //if the CPU is not empty, you need to define that will happen with the process... if it fully preemptive, and there are process pending in higher queue, does the
        //scheduler removes a process from the CPU or does it let it finish its quantum? Make this decision and justify it.
  
        defineCurrentScheduler();
        
        if (cpuEmpty) {
            if (currentScheduler >= 0) {
                schedulers.get(currentScheduler).getNext(true);
            }
            return;
        }
        
        // CPU is busy
        Process running = os.getProcessInCPU();
        if (running != null) {
            int runningScheduler = running.getCurrentScheduler();
            
            // Check if a higher priority scheduler has processes
            if (currentScheduler >= 0 && currentScheduler < runningScheduler) {
                // Preempt current process
                os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, null);
                // After preemption, dispatch highest priority
                if (os.isCPUEmpty()) {
                    defineCurrentScheduler();
                    if (currentScheduler >= 0) {
                        schedulers.get(currentScheduler).getNext(true);
                    }
                }
            } else if (runningScheduler >= 0 && runningScheduler < schedulers.size()) {
                // Continue with current scheduler
                schedulers.get(runningScheduler).getNext(false);
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