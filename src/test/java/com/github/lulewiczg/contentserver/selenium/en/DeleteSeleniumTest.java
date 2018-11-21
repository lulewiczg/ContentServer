package com.github.lulewiczg.contentserver.selenium.en;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.github.lulewiczg.contentserver.selenium.ExpectedMsg;
import com.github.lulewiczg.contentserver.selenium.ExpectedMsgEN;
import com.github.lulewiczg.contentserver.selenium.SeleniumTestTemplate;
import com.github.lulewiczg.contentserver.test.utils.TestUtil;
import com.github.lulewiczg.contentserver.utils.Constants;

/**
 * Tests deleting files.
 * 
 * @author lulewiczg
 */
public class DeleteSeleniumTest extends SeleniumTestTemplate {

    private static List<String> toDelete = new ArrayList<>();

    /**
     * Cleans up files.
     */
    @AfterEach
    public void cleanUp() {
        toDelete.stream().map(File::new).filter(File::exists).forEach(File::delete);
    }

    @Override
    protected ExpectedMsg getMsgs() {
        return new ExpectedMsgEN();
    }

    @Test
    @DisplayName("Delete files")
    public void deleteFiles() throws IOException, InterruptedException {
        login(TEST, TEST);
        gotoShortcut(2);
        clickTableItem(1);
        String path = getPath();
        Stack<String> files = createFiles(2, path);

        driver.navigate().refresh();
        String file = files.pop();
        deleteItem(2, path + Constants.SEP + file);

        driver.navigate().refresh();
        Assertions.assertNull(findElement(file));
        String file2 = files.pop();
        deleteItem(1, path + Constants.SEP + file2);

        driver.navigate().refresh();
        Assertions.assertNull(findElement(file2));
    }

    @Test
    @DisplayName("Delete missing file")
    public void deleteMissingFile() throws IOException, InterruptedException {
        login(TEST, TEST);
        gotoShortcut(2);
        clickTableItem(1);
        String path = getPath();
        Stack<String> files = createFiles(1, path);
        String file = files.pop();
        path += Constants.SEP + file;
        driver.navigate().refresh();

        List<WebElement> buttons = driver.findElements(By.id((DELETE_BTN_ID)));
        buttons.get(0).click();
        new File(path).delete();
        assertAlert(String.format(msg.getDeleteConfirm(), path));
        assertAlert(String.format(msg.getDeleteFailed(), path));
    }

    /**
     * Creates files.
     * 
     * @param num
     *            number of files
     * @param path
     *            path
     * @return files
     * @throws IOException
     *             the IOException
     */
    private Stack<String> createFiles(int num, String path) throws IOException {
        Stack<String> files = TestUtil.createFiles(num, path + Constants.SEP);
        files.sort(String::compareTo);
        toDelete.addAll(files.stream().map(i -> path + Constants.SEP + i).collect(Collectors.toList()));
        return files;
    }

    /**
     * Tries to find element, returns null if not present.
     * 
     * @param name
     *            name
     * @return element
     */
    private WebElement findElement(String name) {
        try {
            return driver.findElement(By.linkText(name));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Deletes given row
     * 
     * @param row
     *            row
     * @param path
     *            file path
     * @throws InterruptedException
     *             the InterruptedException
     */
    private void deleteItem(int row, String path) throws InterruptedException {
        Thread.sleep(1000);
        List<WebElement> buttons = driver.findElements(By.id((DELETE_BTN_ID)));
        buttons.get(row - 1).click();
        assertAlert(String.format(msg.getDeleteConfirm(), path));
    }

}
