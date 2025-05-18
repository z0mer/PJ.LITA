#include <iostream>
#include <string>
#include <vector>
#include <stdexcept>

using namespace std;

int main() {
    int numero;
    bool eh_primo = true;
    int i = 2;
    cout << "Digite um numero:" << endl;
    cin >> numero;
    if (numero <= 1) {
        eh_primo = false;
    } else {
        while (i <= (numero / 2)) {
            if ((numero % i) == 0) {
                eh_primo = false;
                break;
            }
            i = (i + 1);
        }
    }
    if (eh_primo) {
        cout << numero << " è primo!" << endl;
    } else {
        cout << numero << " não é primo!" << endl;
    }
    return 0;
}
