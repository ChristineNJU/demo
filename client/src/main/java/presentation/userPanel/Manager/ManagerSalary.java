package presentation.userPanel.Manager;

import java.util.ArrayList;
import java.util.Vector;

import State.ErrorState;
import State.SalaryType;
import State.UpdateState;
import State.UserRole;
import VO.SalaryVO;
import businesslogic.Impl.Manage.ManageController;
import businesslogic.Service.Manage.ManageService;
import presentation.components.ButtonNew;
import presentation.factory.TableFactory;
import presentation.factory.TableModelFactory;
import presentation.frame.MainFrame;
import presentation.main.FunctionADUS;
import presentation.table.ScrollPaneTable;


public class ManagerSalary extends FunctionADUS{

	ManageService service = new ManageController();
	ArrayList<SalaryVO> salary;
	
	
	public ManagerSalary(){
		buttonNew = new ButtonNew("新增薪水项");
		initUI("薪水管理");
//		panel.remove(buttonNew);
	}
	
	@Override
	protected void initTable() {
		salary = service.showSalary();
		if(salary==null){
			super.isConnectError=true;
			tableV=new Vector<Vector<String>>();
		}
		else if(salary.isEmpty()){
			showError(ErrorState.SEARCHERROR);
			tableV=getVector(salary);
		}
		else {
			tableV = getVector(salary);
		}
		model = TableModelFactory.getSalaryModel(tableV);
		table = TableFactory.getSalaryTable(model);
		table.addMouseListener(tableListener);
		sPanel = new ScrollPaneTable(table);
		panel.add(sPanel);
	}

	@Override
	protected void showSearchResult(String s) {
		
	}

	@Override
	public void performConfirm() {
		ArrayList<SalaryVO> revisedSalary = new ArrayList<SalaryVO>();
		for(int i = 0;i < tableV.size();i++){
			if(model.isUpdate(i)){
				revisedSalary.add(getVO(tableV.get(i)));
			}
		}
		for(SalaryVO temp:revisedSalary){
			UpdateState state = service.updatePayment(temp);
			if(state == UpdateState.CONNECTERROR){
//				showError("连接错误");
				showError(ErrorState.CONNECTERROR);
				break;
			}
			else if(state==UpdateState.NOTFOUND){
				showError(ErrorState.UPDATEERROR);
			}
		}
		
	}

	@Override
	protected SalaryVO getVO(Vector<String> vector) {
		UserRole role = trans.getUserRole(vector.get(0));
		double salary = Double.valueOf(vector.get(1));
		SalaryType salaryType = trans.getSalaryType(vector.get(2));
			
		return new SalaryVO(role, salary, salaryType);
	}

	protected Vector<Vector<String>> getVector(ArrayList<SalaryVO> salaries){
		Vector<Vector<String>> result = new Vector<Vector<String>>();
        for(SalaryVO temp:salaries){
        	Vector<String> vRow = new Vector<String>();
        	vRow.add(trans.getChineseForUserRole(temp.getRole()));
        	vRow.add(trans.getChineseForSalaryType(temp.getType()));
        	vRow.add(temp.getSalary()+"");
        	vRow.add("");
        	
        	result.add(vRow);
        }
		return result;
	}

	@Override
	public void performCancel() {
		MainFrame.changeContentPanel(new ManagerSalary().getPanel());		
	}
}
