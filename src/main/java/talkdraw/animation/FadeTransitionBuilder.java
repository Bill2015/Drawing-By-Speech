package talkdraw.animation;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/** <p>為了建構 {@link #FadeTransition} 而編寫的 Builder</p>
 *  <p>只提供簡單的數值設定</p>
 *  <p>假如要更多的設定選項，還是建議直接宣告 {@link #FadeTransition}</p>
 *  @see FadeTransition*/
final public class FadeTransitionBuilder {
    private FadeTransition fade;
    /** FadeTransitionBuilder 建構子 
     *  @param sec 動畫秒數
     *  @param node 欲動畫的節點 */
    public FadeTransitionBuilder( double sec, Node node ){
        fade = new FadeTransition( Duration.seconds( sec ), node );
    }
    /** 設定延遲時間 
     *  @param sec 延遲秒數 */
    final public FadeTransitionBuilder setDelay( double sec ){
        fade.setDelay( Duration.seconds( sec ) );
        return this;
    }
    /** 設定淡出範圍數值 
     *  @param start 開始數值
     *  @param end 結束數值 */
    final public FadeTransitionBuilder setRange( double start, double end ){
        fade.setFromValue( start );fade.setToValue( end );
        return this;
    }
    /** 設定淡出入起始值 
     *  @param start 開始數值 */
    final public FadeTransitionBuilder setStart( double start ){
        fade.setFromValue( start );
        return this;
    }
    /** <p>建構 {@code FadeTransition} 且為淡出動畫</p>
     *  <p>與 {@link #fadeOut()} 不同，這個沒有設定初始值，要自己設定</p>
     * @return 建構出淡出動畫 {@code [FadeTransition]} */
    final public FadeTransition buildFadeOut(){
        fade.setToValue( 0.0 );
        return fade;
    }
    /** <p>建構 {@code FadeTransition} 且為淡入動畫</p>
     *  <p>與 {@link #fadeIn()} 不同，這個沒有設定初始值，要自己設定</p>
     * @return 建構出淡入動畫 {@code [FadeTransition]} */
    final public FadeTransition buildFadeIn(){
        fade.setToValue( 1.0 );
        return fade;
    }
    /** <p>建構 {@code FadeTransition} 且為淡出動畫</p>
     *  <p>與 {@link #buildFadeOut()} 不同，這個預設值是 1.0 至 0.0</p>
     * @return 建構出淡出動畫 {@code [FadeTransition]} */
    final public FadeTransition fadeOut(){
        fade.setFromValue( 1.0 );
        fade.setToValue( 0.0 );
        return fade;
    }
    /** <p>建構 {@code FadeTransition} 且為淡入動畫</p>
     *  <p>與 {@link #buildFadeIn()} 不同，這個預設值是 0.0 至 1.0</p>
     * @return 建構出淡入動畫 {@code [FadeTransition]} */
    final public FadeTransition fadeIn(){
        fade.setFromValue( 0.0 );
        fade.setToValue( 1.0 );
        return fade;
    }
    /** 建構 {@code FadeTransition} @return 建構出淡出入動畫 {@code [FadeTransition]} */
    final public FadeTransition build(){ return fade; }
    
}