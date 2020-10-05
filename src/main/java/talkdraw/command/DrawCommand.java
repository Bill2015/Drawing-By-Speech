package talkdraw.command;

import java.util.Arrays;

import talkdraw.App;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;
import talkdraw.tools.SelectTool;

/**【指令】 《畫布相關指令》
    * <p>設定畫布大小 輸入寬與高</p>
    *  @param args_0 寬
    *  @param args_1 高 */
public class DrawCommand extends Command{
    /** 已讀數量 */
    private int count=0;
    /** 建構子 */
    public DrawCommand(App APP, CommandAttribute yamlLoader){
        super(APP, yamlLoader);
    }
    @Override
    public NextCommand execute( String ... args ) {
        APP.TOOL_BAR.getNowTool().setMainGC(APP.MAIN.getLayerList().getActiveLayer());
        switch ( phase ) {
            case None:
                if(APP.TOOL_BAR.getNowTool() instanceof SelectTool) return NextCommand.DO_NOTHING;
                try{
                    APP.INFO_PANE.println( APP.TOOL_BAR.getNowTool().speechDraw() );
                }catch(Exception e){ count=0; return failure(e.getMessage());}
                return new NextCommand( this.getClass(), Phase.Get_Value );
            //------------------------
            case Get_Value:
                try{
                    for (int i = count; i < args.length; i++, count++) {
                        switch(args[i].toLowerCase()){
                            case "確定":
                            APP.TOOL_BAR.getNowTool().checkDraw = true;
                            break;
                        }
                       //if(APP.TOOL_BAR.getNowTool().checkDraw)break;
                    }
                    
                    String msg=APP.TOOL_BAR.getNowTool().speechDraw(Arrays.copyOfRange(args, 0, count));
                    if( !msg.equals("true") ){
                        APP.INFO_PANE.println( msg );
                        count=0;
                        return new NextCommand( this.getClass(), phase );
                    }
                    
                    count=0;
                    return success();
                }catch(Exception e){ count=0; return failure(e.getMessage());}
            //------------------------
            default:break;
        }

        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.INFO_PANE.println( "已畫出" );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.INFO_PANE.println( args[0] );
        return NextCommand.DO_NOTHING;
    }
    @Override public NextCommand doTemporaryAction( String ... args ){ return null; }

}