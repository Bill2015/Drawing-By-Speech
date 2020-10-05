package talkdraw.animation;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;


/** <p>為了建構 {@link #ScaleTransition} 而編寫的 Builder</p>
 *  <p>只提供簡單的數值設定</p>
 *  <p>假如要更多的設定選項，還是建議直接宣告 {@link #ScaleTransition}</p>
 *  @see ScaleTransition*/
public class ScaleTransitionBuilder {
    private ScaleTransition scale;
    /** ScaleTransitionBuilder 建構子 
     *  @param sec 動畫秒數
     *  @param node 欲動畫的節點 */
    public ScaleTransitionBuilder( double sec, Node node ){
        scale = new ScaleTransition( Duration.seconds( sec ), node );
    }
    /** ScaleTransitionBuilder 建構子 
     *  @param sec 動畫秒數
     *  @param node 欲動畫的節點 
     *  @param startVal 設定初始值 Scale (X,Y) */
    public ScaleTransitionBuilder( double sec, Node node, double startVal ){
        this( sec , node );
        scale.setFromX( startVal );
        scale.setFromY( startVal );
    }
    /** 設定延遲時間 
     *  @param sec 延遲秒數 */
    final public ScaleTransitionBuilder setDelay( double sec ){
        scale.setDelay( Duration.seconds( sec ) );
        return this;
    }
    /** 設定淡出範圍數值 
     *  @param start 開始數值
     *  @param end 結束數值 */
    final public ScaleTransitionBuilder setRange( double start, double end ){
        scale.setFromX( start );
        scale.setFromY( start );
        scale.setToX( end );
        scale.setToY( end );
        return this;
    }
    /** 設定淡出入起始值 
     *  @param start 開始數值 */
    final public ScaleTransitionBuilder setStart( double start ){
        scale.setFromX( start );
        scale.setFromY( start );
        return this;
    }
    /** 建構 {@code ScaleTransition} @return 建構出淡出入動畫 {@code [ScaleTransition]} */
    final public ScaleTransition build(){ return scale; }

}