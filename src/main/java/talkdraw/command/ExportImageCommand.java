package talkdraw.command;

import javafx.application.Platform;
import talkdraw.App;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;
import talkdraw.dialog.LoadingStage;

/**【指令】 《重作相關指令》
    * <p>復原下一步的動作</p>*/
public class ExportImageCommand extends Command{

    /** 建構子 */
    public ExportImageCommand(App APP, CommandAttribute yamlLoader){
        super(APP, yamlLoader);
    }
    @Override
    public NextCommand execute( String ... args ) {

        switch ( phase ) {
            case None:
                Platform.runLater(() -> {
                    new LoadingStage("生成 PNG 中...", "成功！", APP.PRIMARY_STAGE, LoadingStage.DEFAULT_TIME ).show();
                });
                APP.FILE_PROCCESSOR.SaveAsPNG();
                return success();
            //------------------------
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.println( "成功輸出圖檔" , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.println( "輸出圖檔錯誤" , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }
    @Override public NextCommand doTemporaryAction( String ... args ){ return null; }

}