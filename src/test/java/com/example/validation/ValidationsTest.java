package com.example.validation

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class ValidationsTest {

  @Test
  public void 成功するとこんな感じで() {
    ValidationResult result = Validations.notNull("sample");

    assertThat(result.isValid()).isEqualTo(true);
  }

  @Test
  public void 失敗するとこんな感じ() {
    ValidationResult result = Validations.notNull(null);

    assertThat(result.isValid()).isEqualTo(false);
  }

  @Test
  public void isInvalidもあると便利そうかな() {
    ValidationResult result = Validations.notNull("sample");

    assertThat(result.isInvalid()).isEqualTo(false);
  }

  @Test
  public void 成功したときは結果は空っぽで() {
    ValidationResult result = Validations.notNull("sample");

    assertThat(result.getFailure().isPresent()).isEqualTo(false);
  }

  @Test
  public void 失敗したときは結果が入ってるんだけど() {
    ValidationResult result = Validations.notNull(null);

    assertThat(result.getFailure().get()).isEqualTo("The value cannot be null");
  }

  @Test
  public void 実際は失敗結果はリストで保持されてる() {
    ValidationResult result = Validations.notNull(null);

    assertThat(result.getFailures().get(0)).isEqualTo("The value cannot be null");
  }

  @Test
  public void 失敗してるときは例外を投げることができて() {
    ValidationResult result = Validations.notNull(null);

    assertThatIllegalArgumentException()
        .isThrownBy(result::throwIfInvalid)
        .withMessage("The value cannot be null");
  }

  @Test
  public void 成功したときに呼び出しても何もおこらない() {
    ValidationResult result = Validations.notNull("sample");

    assertThatCode(result::throwIfInvalid)
        .doesNotThrowAnyException();
  }

  @Test
  public void 自分でチェックを定義することもできるし() {
    String target = "sample";
    ValidationResult result = Validations.isTrue(() -> target.length() > 5);

    assertThat(result.isValid()).isEqualTo(true);
  }

  @Test
  public void 自分で失敗結果の生成を指定することもできる() {
    class MyError {
      String myValue;
    }

    String target = "sample";
    ValidationResult result = Validations.notNull(target, () -> {
      MyError myError = new MyError();
      myError.myValue = String.format("The value is invalid: %s", target);
    });

    assertThat(result.getFailure().myValue).
        isEqualTo("The value is invalid: sample");
  }

  @Test
  public void 複数つなげると成功した時だけ次が実行されて() {
    String target = "sample";
    ValidationResult result = Validations
        .notNull(target, () -> "ここは成功する")
        .isTrue(() -> target.length() > 10, () -> "ここで失敗する");

    assertThat(result.getFailure()).isEqualTo("ここで失敗する");
  }

  @Test
  public void 失敗したらそこで終わる() {
    String target = null;
    ValidationResult result = Validations
        .notNull(target, () -> "ここで失敗する")
        .isTrue(() -> target.length() > 10, () -> "この処理は実行されない");

    assertThat(result.getFailure()).isEqualTo("ここで失敗する");
  }

  @Test
  public void 失敗しても次のチェックを実行したいときはこんな感じで書けて() {
    String target1 = null;
    String target2 = "sample";
    String target3 = null;
    ValidationResult result = Validations.checkAll(
        Validations.notNull(target1),
        Validations.notNull(target2),
        Validations.notNull(target3)
    );

    assertThat(result.isValid()).isEqualTo(false);
    assertThat(result.getFailures().size()).isEqualTo(2);
    assertThat(result.getFailures().get(0)).isEqualTo("Failure1");
    assertThat(result.getFailures().get(1)).isEqualTo("Failure3");
  }

  @Test
  public void 全部入りはこんな感じになるといいのかなぁ() {
    Integer target1 = 30;
    Integer target2 = 50;
    ValidationResult result = Validations.checkAll(
        Validations.notNull(target1),
        Validations.notNull(target2)
    ).isTrue(
        () -> target1 > target2,
        () -> String.format("target1 > target2 じゃないとだめだよ: target1=%d target2=%d", target1, target2)
    );

    assertThat(result.isValid()).isEqualTo(false);
    assertThat(result.getFailure().get()).isEqualTo("target1 > target2 じゃないとだめだよ: target1=30 target2=50");
  }

  @Test
  public void おまけ() {
    // notNull
    assertThat(Validations.notNull(null).isValid()).isEqualTo(false);
    assertThat(Validations.notNull("").isValid()).isEqualTo(true);

    // notEmpty
    assertThat(Validations.notEmpty(null).isValid()).isEqualTo(false);
    assertThat(Validations.notEmpty("").isValid()).isEqualTo(false);
    assertThat(Validations.notEmpty("sample").isValid()).isEqualTo(true);

    // あとは数字のメソッドとかあってもいいなぁ
  }

  public void staticインポートをキープするために書いてるだけ() {
    assertThat(true).isEqualTo(false);
    assertThatCode(() -> {
      throw new RuntimeException();
    });
  }
}
